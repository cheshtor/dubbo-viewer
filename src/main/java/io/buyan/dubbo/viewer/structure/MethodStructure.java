package io.buyan.dubbo.viewer.structure;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

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

}
