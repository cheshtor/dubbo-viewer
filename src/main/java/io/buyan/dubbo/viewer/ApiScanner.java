package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.model.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

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
     * 要扫描的包路径，避免扫描范围扩大导致扫描所需依赖增多
     */
    private List<String> packages = new ArrayList<>();

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
    public ParsedResult scanApi() {
        ParsedResult parsedResult = new ParsedResult();
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
            return parsedResult;
        }
        // 创建类加载器，将要扫描的 jar 包作为其 classpath
        ApiClassLoader classLoader = new ApiClassLoader(urls);
        // 按 jar 包范围逐一扫描 Dubbo 接口
        jarClasses.forEach((jarName, classes) -> {
            Map<String, String> errors = new HashMap<>();
            ParsedJar parsedJar = new ParsedJar();
            parsedJar.setJarName(jarName);
            parsedJar.setErrors(errors);
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
                        ParsedInterface parsedInterface = new ParsedInterface();
                        parsedInterface.setClassName(clazz.getName());
                        // 解析接口内的方法
                        Method[] declaredMethods = clazz.getDeclaredMethods();
                        for (Method method : declaredMethods) {
                            ParsedMethod parsedMethod = analyzeMethod(method);
                            parsedInterface.addParsedMethod(parsedMethod);
                        }
                        parsedJar.addParsedInterface(parsedInterface);
                    }
                } catch (Throwable e) {
                    errors.put(name, e.getClass().getSimpleName() + ":" + e.getMessage());
                    log.error("Can not find class {}", name);
                }
            }
            parsedResult.addParsedJar(parsedJar);
        });
        // 接口解析完成后卸载类加载器及其所加载的类
        try {
            classLoader.close();
        } catch (IOException e) {
            log.error("Close ClassLoader failed.", e);
        }
        return parsedResult;
    }

    /**
     * 解析方法
     *
     * @param method 目标方法
     * @return 结构化的方法
     */
    private ParsedMethod analyzeMethod(Method method) {
        ParsedMethod parsedMethod = new ParsedMethod();
        parsedMethod.setMethodName(method.getName());
        parsedMethod.setReturnType(getReturnType(method.getGenericReturnType()));
        parsedMethod.setMethodParams(getMethodParams(method.getParameterTypes()));
        return parsedMethod;
    }

    /**
     * 解析方法参数
     *
     * @param parameterTypes 方法参数类型
     * @return 结构化的方法参数
     */
    private List<MethodParam> getMethodParams(Type[] parameterTypes) {
        List<MethodParam> methodParams = new ArrayList<>();
        int argIndex = 0;
        for (Type type : parameterTypes) {
            MethodParam param = new MethodParam();
            param.setIndex(argIndex++);
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type rawType = parameterizedType.getRawType();
                param.setRawType(rawType.getTypeName());
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type at : actualTypeArguments) {
                    param.addParameterizedType(at.getTypeName());
                }
            } else {
                param.setRawType(type.getTypeName());
            }
            methodParams.add(param);
        }
        return methodParams;
    }

    /**
     * 解析方法返回值
     *
     * @param returnType 方法返回值类型
     * @return 结构化的方法返回值
     */
    private MethodReturnType getReturnType(Type returnType) {
        MethodReturnType methodReturnType = new MethodReturnType();
        if (returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) returnType;
            Type rawType = parameterizedType.getRawType();
            methodReturnType.setRawType(rawType.getTypeName());
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            for (Type type : actualTypeArguments) {
                methodReturnType.addParameterizedType(type.getTypeName());
            }
        } else {
            methodReturnType.setRawType(returnType.getTypeName());
        }
        return methodReturnType;
    }

}
