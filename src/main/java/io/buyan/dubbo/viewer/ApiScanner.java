package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.model.MethodParam;
import io.buyan.dubbo.viewer.model.MethodReturnType;
import io.buyan.dubbo.viewer.model.ParsedClass;
import io.buyan.dubbo.viewer.model.ParsedMethod;
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
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
@Slf4j
public class ApiScanner {

    private URL[] urls;

    private List<JarFile> jarFiles = new ArrayList<>();

    private List<String> packages = new ArrayList<>();

    public ApiScanner(File[] files, String... basePackages) {
        if (null == files) {
            return;
        }
        List<URL> urlList = new ArrayList<>();
        for (File file : files) {
            try {
                URL url = file.toURI().toURL();
                urlList.add(url);
            } catch (MalformedURLException e) {
                log.error("Translate File {} to URL failed.", file.getAbsolutePath());
            }
            try {
                JarFile jarFile = new JarFile(file);
                jarFiles.add(jarFile);
            } catch (IOException e) {
                log.error("Translate File {} to JarFile failed.", file.getAbsolutePath());
            }
        }
        urls = urlList.toArray(new URL[urlList.size()]);
        if (null != basePackages) {
            packages = Arrays.stream(basePackages).map(p -> p.replace(".", "/")).collect(Collectors.toList());
        }
    }

    private boolean isClassInBasePackages(String name) {
        Optional<String> exists = packages.stream().filter(name::startsWith).findAny();
        return exists.isPresent();
    }

    public List<ParsedClass> scanApi() {
        SdkClassLoader classLoader = new SdkClassLoader(urls);

        List<String> binaryNames = new ArrayList<>();
        for (JarFile jarFile : jarFiles) {
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

        List<ParsedClass> parsedClasses = new ArrayList<>();
        for (String name : binaryNames) {
            try {
                Class<?> clazz = classLoader.loadClass(name, false);
                if (null == clazz) {
                    continue;
                }
                if (clazz.getDeclaredMethods().length == 0) {
                    continue;
                }
                if (clazz.isInterface()) {
                    ParsedClass parsedClass = new ParsedClass();
                    parsedClass.setClassName(clazz.getName());
                    Method[] declaredMethods = clazz.getDeclaredMethods();
                    for (Method method : declaredMethods) {
                        ParsedMethod parsedMethod = analyzeMethod(method);
                        parsedClass.addParsedMethod(parsedMethod);
                    }
                    parsedClasses.add(parsedClass);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return parsedClasses;
    }

    private ParsedMethod analyzeMethod(Method method) {
        ParsedMethod parsedMethod = new ParsedMethod();
        parsedMethod.setMethodName(method.getName());
        parsedMethod.setReturnType(getReturnType(method.getReturnType()));
        parsedMethod.setMethodParams(getMethodParams(method.getParameterTypes()));
        return parsedMethod;
    }

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
