package io.buyan.dubbo.viewer;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public class SdkClassLoader extends URLClassLoader {

    public SdkClassLoader(URL[] urls) {
        super(urls);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        final Class<?> loadedClass = findLoadedClass(name);
        if (loadedClass != null) {
            return loadedClass;
        }

        try {
            Class<?> aClass = findClass(name);
            if (resolve) {
                resolveClass(aClass);
            }
            return aClass;
        } catch (Exception e) {
            return super.loadClass(name, resolve);
        }
    }

}
