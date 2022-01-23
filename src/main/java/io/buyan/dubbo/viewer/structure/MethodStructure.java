package io.buyan.dubbo.viewer.structure;

import io.buyan.dubbo.viewer.StructureResolver;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方法结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
@Data
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

    public String getReturnTypeDeclaring() {
        return StructureResolver.read(returnType);
    }

    public String getMethodDeclaring() {
        String paramList = params.stream().map(MethodParamStructure::getParamDeclaring).collect(Collectors.joining(", "));
        return getReturnTypeDeclaring() + " " + methodName + "(" + paramList +  ");";
    }


}
