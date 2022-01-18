package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.util.List;

/**
 * 方法模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class ParsedMethod {

    private String methodName;

    private MethodReturnType returnType;

    private List<MethodParam> methodParams;

}
