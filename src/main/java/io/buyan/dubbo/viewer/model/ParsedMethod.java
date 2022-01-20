package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 方法模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class ParsedMethod implements Serializable {

    private String methodName;

    private MethodReturnType returnType;

    private List<MethodParam> methodParams;

    private String method;

    private String simpleMethod;

    public String getMethod() {
        String params = methodParams.stream().map(MethodParam::getParam).collect(Collectors.joining(", "));
        return returnType.getReturnType() + " " + methodName + "(" + params + ");";
    }

    public String getSimpleMethod() {
        String params = methodParams.stream().map(MethodParam::getSimpleParam).collect(Collectors.joining(", "));
        return returnType.getSimpleReturnType() + " " + methodName + "(" + params + ");";
    }

}
