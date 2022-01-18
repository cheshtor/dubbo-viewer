package io.buyan.dubbo.viewer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
@Slf4j
public class ApiClassLoader extends URLClassLoader {

    private List<Jar> jars;

    private final ReentrantLock lock = new ReentrantLock();

    public ApiClassLoader(URL[] urls) {
        super(urls);
        initClasspath(urls);
    }

    public Jar getJar(String name) {
        String path = name.replace(".", "/").concat(".class");
        for (Jar jar : jars) {
            JarEntry jarEntry = jar.getJarFile().getJarEntry(path);
            if (null != jarEntry) {
                return jar;
            }
        }
        return null;
    }

    public byte[] loadClassBytes(String name) {
        String path = name.replace(".", "/").concat(".class");
        Jar jar = getJar(name);
        if (null != jar) {
            try {
                URL classFileURL = new URL("jar:file:" + jar.getSourceFile().getAbsolutePath() + "!/" + path);
                byte[] data;
                try (final BufferedInputStream bis = new BufferedInputStream(classFileURL.openStream());
                     final ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
                    int ch;
                    while ((ch = bis.read()) != -1) {
                        baos.write(ch);
                    }
                    data = baos.toByteArray();
                }
                return data;
            } catch (IOException e) {
                log.error("Find class {} failed", name, e);
            }
        }
        return null;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        byte[] bytes = loadClassBytes(name);
        if (null != bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
        return null;
//        throw new ClassNotFoundException("Can not find class " + name);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }
        try {
            Class<?> aClass = findClass(name);
            if (null == aClass) {
                return null;
            }
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            return super.loadClass(name, resolve);
        }
    }

    private void initClasspath(URL[] urls) {
        if (null == jars) {
            lock.lock();
            try {
                if (null == jars) {
                    jars = new ArrayList<>();
                    for (URL url : urls) {
                        try {
                            File sourceFile = new File(url.getFile());
                            JarFile jarFile = new JarFile(sourceFile);
                            jars.add(new Jar(jarFile, sourceFile));
                        } catch (IOException e) {
                            log.error("Translate URL {} to JarFile failed.", url.getPath());
                        }
                    }
                }
            } finally {
                lock.unlock();
            }
        }
    }

    @Getter
    private static class Jar {
        private final JarFile jarFile;
        private final File sourceFile;

        public Jar(JarFile jarFile, File sourceFile) {
            this.jarFile = jarFile;
            this.sourceFile = sourceFile;
        }
    }
}
