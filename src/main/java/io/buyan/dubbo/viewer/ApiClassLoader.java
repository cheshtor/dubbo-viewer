package io.buyan.dubbo.viewer;

import java.net.URL;
import java.net.URLClassLoader;

/**
 * 专用于 Dubbo API 扫描的类加载器。每次扫描都需要新建一个实例，并且在扫描完成后调用 close() 方法
 * 关闭类加载器。这样在 JDK6+ 的环境下可以通知 GC 尽快的卸载掉类加载器以及加载的类，释放内存空间。
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public class ApiClassLoader extends URLClassLoader {

    public ApiClassLoader(URL[] urls) {
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
