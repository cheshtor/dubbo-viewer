package io.buyan.dubbo.viewer.utils;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/24
 */
public class TypeUtil {

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
