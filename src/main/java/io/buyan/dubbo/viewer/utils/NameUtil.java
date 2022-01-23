package io.buyan.dubbo.viewer.utils;

/**
 * 名称工具
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/19
 */
public class NameUtil {

    /**
     * 将全类名转换为简单类名
     * @param binaryName 全类名
     * @return 简单类名
     */
    public static String simplify(String binaryName) {
        return binaryName.substring(binaryName.lastIndexOf(".") + 1);
    }

}
