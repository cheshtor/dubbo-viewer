package io.buyan.dubbo.viewer.utils;

/**
 * Java 类型工具
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/24
 */
public class TypeUtil {

    /**
     * 判断是否为基本类型
     * @param typeName 类型名称
     * @return true 基本类型 false 非基本类型
     */
    public static boolean isPrimitive(String typeName) {
        return typeName.equals("byte") ||
                typeName.equals("short") ||
                typeName.equals("int") ||
                typeName.equals("long") ||
                typeName.equals("char") ||
                typeName.equals("boolean") ||
                typeName.equals("double") ||
                typeName.equals("float");
    }

}
