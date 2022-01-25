package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.structure.*;
import io.buyan.dubbo.viewer.utils.TypeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

import static io.buyan.dubbo.viewer.StructureResolver.findCustomType;

/**
 * Dubbo API 扫描器
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public class ApiScanner {

    private static Logger log = LoggerFactory.getLogger(ApiScanner.class);

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
    private List<String> packages;

    /**
     * 扫描过程中的错误。key => 文件名，value => 错误信息
     */
    private Map<String, String> globalError = new HashMap<>();

    public ApiScanner(File[] files, String... basePackages) {
        if (null == files) {
            throw new ApiScanException("There are no files to scan.");
        }
        if (null == basePackages || basePackages.length == 0) {
            throw new ApiScanException("basePackages can not be null.");
        }
        // File 转换为 URL 和 JarFile
        List<URL> urlList = new ArrayList<>();
        for (File file : files) {
            // File 转为 URL
            try {
                URL url = file.toURI().toURL();
                urlList.add(url);
            } catch (MalformedURLException e) {
                globalError.put(file.getAbsolutePath(), e.getMessage());
                log.error("Translate File {} to URL failed.", file.getAbsolutePath());
            }
            // File 转为 JarFile
            try {
                JarFile jarFile = new JarFile(file);
                jarFiles.add(jarFile);
            } catch (IOException e) {
                globalError.put(file.getAbsolutePath(), e.getMessage());
                log.error("Translate File {} to JarFile failed.", file.getAbsolutePath());
            }
        }
        urls = urlList.toArray(new URL[urlList.size()]);
        packages = Arrays.stream(basePackages).map(p -> p.replace(".", "/")).collect(Collectors.toList());
    }

    /**
     * 判断指定的类是否在要扫描的包范围内
     * @param name 类型名称
     * @return true 可被扫描 false 不可扫描
     */
    private boolean isClassInBasePackages(String name) {
        if (packages.isEmpty()) {
            return false;
        }
        Optional<String> exists = packages.stream().filter(name::startsWith).findAny();
        return exists.isPresent();
    }

    /**
     * 对所有的 jar 包进行扫描，分析出所有的接口及其方法
     *
     * @return Result 结构化的 Dubbo 接口定义
     */
    public Result scanApi() {
        // 每次扫描都必须新建 ApiScanner 实例以确保内存及时回收
        if (classLoader != null) {
            throw new ApiScanException("ApiScanner instance can not be reused.");
        }
        // 创建类加载器，urls 为 classpath
        classLoader = new ApiClassLoader(urls);
        // 扫描结果
        Result result = new Result();
        result.setGlobalError(globalError);
        // Jar 包抽象结构集合
        List<JarStructure> jarStructures = new ArrayList<>();
        result.setJars(jarStructures);
        // 查找要解析的 Dubbo 接口
        Map<String, List<String>> jarClasses = new HashMap<>();
        for (JarFile jarFile : jarFiles) {
            List<String> binaryNames = new ArrayList<>();
            String jarName = jarFile.getName();
            jarName = jarName.substring(jarName.lastIndexOf("/") + 1);
            jarClasses.put(jarName, binaryNames);
            // 迭代 Jar 包内的文件
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry jarEntry = entries.nextElement();
                String name = jarEntry.getName();
                // 筛选在扫描范围内的 .class 文件
                if (name.endsWith(".class") && isClassInBasePackages(name)) {
                    String binaryName = name.substring(0, name.lastIndexOf(".")).replace("/", ".");
                    binaryNames.add(binaryName);
                }
            }
        }
        if (jarClasses.isEmpty()) {
            return result;
        }
        // 缓存 MethodStructure 用于之后的入参类型分析
        List<MethodStructure> cachedMethodStructures = new ArrayList<>();
        // 按 Jar 包范围逐一扫描 Dubbo 接口
        jarClasses.forEach((jarName, classes) -> {
            // Jar 包扫描错误
            Map<String, String> errors = new HashMap<>();
            // Jar 包抽象
            JarStructure jarStructure = new JarStructure();
            // Jar 包名称
            jarStructure.setJarName(jarName);
            jarStructure.setErrors(errors);
            // Jar 包内的所有接口抽象
            List<InterfaceStructure> interfaces = new ArrayList<>();
            jarStructure.setInterfaces(interfaces);
            // 解析 Dubbo 接口
            for (String name : classes) {
                try {
                    // 加载但不链接类，因为只需要分析类结构
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
                    // 只处理接口
                    if (clazz.isInterface()) {
                        // 接口抽象
                        InterfaceStructure interfaceStructure = new InterfaceStructure();
                        // 设置接口类名
                        interfaceStructure.setClassname(clazz.getName());
                        // 接口下的所有方法抽象
                        List<MethodStructure> methodStructures = new ArrayList<>();
                        interfaceStructure.setMethods(methodStructures);
                        // 解析接口内的方法
                        Method[] declaredMethods = clazz.getDeclaredMethods();
                        for (Method method : declaredMethods) {
                            MethodStructure methodStructure = analyzeMethod(method);
                            methodStructures.add(methodStructure);
                            cachedMethodStructures.add(methodStructure);
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
        // 将所有方法的入参的 Field 进行解析，拿到涉及到的所有自定义类型的字段定义
        Map<String, Map<String, String>> properties = getCustomTypeProperties(cachedMethodStructures);
        result.setBeanProperty(properties);
        // 接口解析完成后卸载类加载器及其所加载的类
        try {
            classLoader.close();
        } catch (IOException e) {
            log.error("Close ClassLoader failed.", e);
        }
        return result;
    }

    /**
     * 获取所有方法入参类型的 Field 中涉及到的自定义类型的字段定义
     * @param methodStructures 方法结构列表
     * @return 类型与类型定义字段的映射
     */
    private Map<String, Map<String, String>> getCustomTypeProperties(List<MethodStructure> methodStructures) {
        Set<String> typeNames = findCustomTypeInMethodParam(methodStructures);
        Map<String, Map<String, String>> properties = new HashMap<>();
        for (String typeName : typeNames) {
            try {
                Class<?> clazz = classLoader.loadClass(typeName);
                Field[] fields = clazz.getDeclaredFields();
                Map<String, String> property = new HashMap<>();
                for (Field field : fields) {
                    property.put(field.getName(), field.getGenericType().getTypeName());
                }
                properties.put(typeName, property);
            } catch (ClassNotFoundException e) {
                globalError.put(typeName, e.getClass().getSimpleName() + ":" + e.getMessage());
                log.error("Can not find class {}", typeName);
            }
        }
        return properties;
    }

    /**
     * 解析所有被扫描的接口方法的入参、以及入参的 Field 中使用到的自定义类型
     * @param methodStructures 方法结构
     * @return 自定义类型
     */
    private Set<String> findCustomTypeInMethodParam(List<MethodStructure> methodStructures) {
        Set<String> typeNames = new HashSet<>();
        for (MethodStructure methodStructure : methodStructures) {
            List<MethodParamStructure> params = methodStructure.getParams();
            for (MethodParamStructure param : params) {
                Set<String> names = new HashSet<>();
                findCustomTypeInField(param.getTypeStructure(), names);
                typeNames.addAll(names);
            }
        }
        return typeNames;
    }

    /**
     * 解析 TypeStructure 表示的类型的所有 Field，并找出这些 Field 使用的自定义类型
     * @param typeStructure TypeStructure
     * @param names 找到的所有自定义类型全类名
     */
    private void findCustomTypeInField(TypeStructure typeStructure, Set<String> names) {
        Set<String> customType = findCustomType(typeStructure);
        names.addAll(customType);
        for (String name : customType) {
            try {
                Class<?> clazz = classLoader.loadClass(name, false);
                Field[] fields = clazz.getDeclaredFields();
                for (Field field : fields) {
                    Type fieldType = field.getGenericType();
                    String typeName = fieldType.getTypeName();
                    if (TypeUtil.isPrimitive(typeName)) {
                        continue;
                    }
                    if (clazz.isEnum()) {
                        continue;
                    }
                    TypeStructure fieldTypeStructure = getTypeStructureChain(fieldType);
                    if (!fieldTypeStructure.getGenericTypes().isEmpty()) {
                        findCustomTypeInField(fieldTypeStructure, names);
                    }
                }
            } catch (ClassNotFoundException e) {
                globalError.put(name, e.getClass().getSimpleName() + ":" + e.getMessage());
                log.error("Can not find class {}", name);
            }
        }
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
            TypeStructure typeStructure = getTypeStructureChain(parameterType);
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
        return getTypeStructureChain(returnType);
    }

    /**
     * 将指定类型解析为 TypeStructure
     * @param type 要解析的类型
     * @return TypeStructure
     */
    private TypeStructure getTypeStructureChain(Type type) {
        TypeStructure typeStructure = new TypeStructure();
        typeStructure.setRawType(type);
        typeStructure.setTypeName(type.getTypeName());
        return StructureResolver.buildTypeStructureChain(typeStructure);
    }

}
