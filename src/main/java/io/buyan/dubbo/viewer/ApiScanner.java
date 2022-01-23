package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.structure.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

import static io.buyan.dubbo.viewer.StructureResolver.readBean;

/**
 * Dubbo 接口扫描器
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
@Slf4j
public class ApiScanner {

    /**
     * 要扫描的 jar 包
     */
    private URL[] urls;
    private List<JarFile> jarFiles = new ArrayList<>();

    /**
     * 类加载器
     */
    private ApiClassLoader classLoader;

    /**
     * 要扫描的包路径，避免扫描范围扩大导致扫描所需依赖增多
     */
    private List<String> packages = new ArrayList<>();

    /**
     * 扫描过程中的错误。key => 文件名，value => 错误信息
     */
    private Map<String, String> fileTranslateError = new HashMap<>();

    public ApiScanner(File[] files, String... basePackages) {
        if (null == files) {
            return;
        }
        List<URL> urlList = new ArrayList<>();
        for (File file : files) {
            // File 转为 URL
            try {
                URL url = file.toURI().toURL();
                urlList.add(url);
            } catch (MalformedURLException e) {
                fileTranslateError.put(file.getAbsolutePath(), e.getMessage());
                log.error("Translate File {} to URL failed.", file.getAbsolutePath());
            }
            // File 转为 JarFile
            try {
                JarFile jarFile = new JarFile(file);
                jarFiles.add(jarFile);
            } catch (IOException e) {
                fileTranslateError.put(file.getAbsolutePath(), e.getMessage());
                log.error("Translate File {} to JarFile failed.", file.getAbsolutePath());
            }
        }
        urls = urlList.toArray(new URL[urlList.size()]);
        if (null != basePackages) {
            packages = Arrays.stream(basePackages).map(p -> p.replace(".", "/")).collect(Collectors.toList());
        }
    }

    private boolean isClassInBasePackages(String name) {
        if (packages.isEmpty()) {
            return true;
        }
        Optional<String> exists = packages.stream().filter(name::startsWith).findAny();
        return exists.isPresent();
    }

    /**
     * 对所有的 jar 包进行扫描，分析出所有的接口及其方法
     *
     * @return 结构化的 Dubbo 接口定义
     */
    public Result scanApi() {
        // 每次扫描都必须新建 ApiScanner 实例以确保内存及时回收
        if (classLoader != null) {
            throw new RuntimeException("ApiScanner instance can not be reused.");
        }
        classLoader = new ApiClassLoader(urls);
        Result result = new Result();
        List<JarStructure> jarStructures = new ArrayList<>();
        result.setJars(jarStructures);
        // 查找要解析的 Dubbo 接口
        Map<String, List<String>> jarClasses = new HashMap<>();
        for (JarFile jarFile : jarFiles) {
            List<String> binaryNames = new ArrayList<>();
            String jarName = jarFile.getName();
            jarName = jarName.substring(jarName.lastIndexOf("/") + 1);
            jarClasses.put(jarName, binaryNames);
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                if (name.endsWith(".class") && isClassInBasePackages(name)) {
                    String binaryName = name.substring(0, name.lastIndexOf(".")).replace("/", ".");
                    binaryNames.add(binaryName);
                }
            }
        }
        if (jarClasses.isEmpty()) {
            return result;
        }
        // 按 jar 包范围逐一扫描 Dubbo 接口
        jarClasses.forEach((jarName, classes) -> {
            Map<String, String> errors = new HashMap<>();
            JarStructure jarStructure = new JarStructure();
            jarStructure.setJarName(jarName);
            jarStructure.setErrors(errors);
            List<InterfaceStructure> interfaces = new ArrayList<>();
            jarStructure.setInterfaces(interfaces);
            // 解析 Dubbo 接口
            for (String name : classes) {
                try {
                    Class<?> clazz = classLoader.loadClass(name, false);
                    if (null == clazz) {
                        errors.put(name, "Try to load this class but return null.");
                        continue;
                    }
                    // 跳过没有定义任何方法的接口
                    if (clazz.getDeclaredMethods().length == 0) {
                        errors.put(name, "This class has no methods.");
                        continue;
                    }
                    if (clazz.isInterface()) {
                        InterfaceStructure interfaceStructure = new InterfaceStructure();
                        interfaceStructure.setClassname(clazz.getName());
                        List<MethodStructure> methodStructures = new ArrayList<>();
                        interfaceStructure.setMethods(methodStructures);
                        // 解析接口内的方法
                        Method[] declaredMethods = clazz.getDeclaredMethods();
                        for (Method method : declaredMethods) {
                            MethodStructure methodStructure = analyzeMethod(method);
                            methodStructures.add(methodStructure);
                        }
                        interfaces.add(interfaceStructure);
                    }
                } catch (Throwable e) {
                    errors.put(name, e.getClass().getSimpleName() + ":" + e.getMessage());
                    log.error("Can not find class {}", name);
                }
            }
            jarStructures.add(jarStructure);
        });
        List<String> beanNames = new ArrayList<>();
        for (JarStructure jarStructure : jarStructures) {
            List<InterfaceStructure> interfaces = jarStructure.getInterfaces();
            for (InterfaceStructure itf : interfaces) {
                List<MethodStructure> methods = itf.getMethods();
                for (MethodStructure method : methods) {
                    // 只解析方法参数，不解析返回值。因为发起请求时用户只需要填入请求参数，而不需要关心返回值的类型
                    List<MethodParamStructure> params = method.getParams();
                    for (MethodParamStructure param : params) {
                        TypeStructure typeStructure = param.getTypeStructure();
                        readBean(typeStructure, beanNames);
                    }
                }
            }
        }
        if (!beanNames.isEmpty()) {
            Map<String, Map<String, String>> beanProperty = new HashMap<>();
            for (String beanName : beanNames) {
                try {
                    Class<?> clazz = classLoader.loadClass(beanName, false);
                    Field[] fields = clazz.getDeclaredFields();
                    Map<String, String> nameTypeMapping = new HashMap<>();
                    for (Field field : fields) {
                        String fieldName = field.getName();
                        String typeName = field.getGenericType().getTypeName();
                        nameTypeMapping.putIfAbsent(fieldName, typeName);
                    }
                    beanProperty.put(beanName, nameTypeMapping);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            result.setBeanProperty(beanProperty);
        }
        // 接口解析完成后卸载类加载器及其所加载的类
        try {
            classLoader.close();
        } catch (IOException e) {
            log.error("Close ClassLoader failed.", e);
        }
        return result;
    }

    /**
     * 解析方法
     *
     * @param method 目标方法
     * @return 结构化的方法
     */
    private MethodStructure analyzeMethod(Method method) {
        MethodStructure methodStructure = new MethodStructure();
        methodStructure.setMethodName(method.getName());
        methodStructure.setReturnType(getReturnType(method.getGenericReturnType()));
        methodStructure.setParams(getMethodParams(method.getGenericParameterTypes()));
        return methodStructure;
    }

    /**
     * 解析方法参数
     *
     * @param parameterTypes 方法参数类型
     * @return 结构化的方法参数
     */
    private List<MethodParamStructure> getMethodParams(Type[] parameterTypes) {
        List<MethodParamStructure> params = new ArrayList<>();
        int argIndex = 0;
        for (Type parameterType : parameterTypes) {
            MethodParamStructure methodParamStructure = new MethodParamStructure();
            TypeStructure typeStructure = new TypeStructure();
            typeStructure.setRawType(parameterType);
            typeStructure.setTypeName(parameterType.getTypeName());
            typeStructure = StructureResolver.buildTypeStructure(typeStructure);
            methodParamStructure.setName("arg" + (argIndex++));
            methodParamStructure.setTypeStructure(typeStructure);
            params.add(methodParamStructure);
        }
        return params;
    }

    /**
     * 解析方法返回值
     *
     * @param returnType 方法返回值类型
     * @return 结构化的方法返回值
     */
    private TypeStructure getReturnType(Type returnType) {
        TypeStructure typeStructure = new TypeStructure();
        typeStructure.setRawType(returnType);
        typeStructure.setTypeName(returnType.getTypeName());
        typeStructure = StructureResolver.buildTypeStructure(typeStructure);
        return typeStructure;
    }

}
