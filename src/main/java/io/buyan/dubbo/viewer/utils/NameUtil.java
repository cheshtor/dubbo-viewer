package io.buyan.dubbo.viewer.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public static List<String> batchSimplify(List<String> binaryNames) {
        return binaryNames.stream().map(NameUtil::simplify).collect(Collectors.toList());
    }

}
