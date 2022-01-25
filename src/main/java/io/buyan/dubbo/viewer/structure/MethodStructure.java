package io.buyan.dubbo.viewer.structure;

import io.buyan.dubbo.viewer.StructureResolver;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方法结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class MethodStructure implements Serializable {

    /**
     * 方法名
     */
    private String methodName;

    /**
     * 方法返回值类型结构
     */
    private TypeStructure returnType;

    /**
     * 方法参数类型结构
     */
    private List<MethodParamStructure> params;

    /**
     * 获取方法返回值类型的字符串描述
     * @return 方法返回值类型的字符串描述
     */
    public String getReturnTypeDeclaring() {
        return StructureResolver.restoreLiteralTypeDeclaring(returnType);
    }

    /**
     * 获取整个方法的签名
     * @return 整个方法的签名
     */
    public String getMethodDeclaring() {
        String paramList = params.stream().map(MethodParamStructure::getParamDeclaring).collect(Collectors.joining(", "));
        return getReturnTypeDeclaring() + " " + methodName + "(" + paramList +  ");";
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public TypeStructure getReturnType() {
        return returnType;
    }

    public void setReturnType(TypeStructure returnType) {
        this.returnType = returnType;
    }

    public List<MethodParamStructure> getParams() {
        return params;
    }

    public void setParams(List<MethodParamStructure> params) {
        this.params = params;
    }
}
