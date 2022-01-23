package io.buyan.dubbo.viewer.structure;

import io.buyan.dubbo.viewer.StructureResolver;
import lombok.Data;

import java.io.Serializable;

/**
 * 方法参数结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
@Data
public class MethodParamStructure implements Serializable {

    /**
     * 参数名称
     */
    private String name;

    /**
     * 参数类型结构
     */
    private TypeStructure typeStructure;

    /**
     * 字符串形式的参数表示
     */
    public String getParamDeclaring() {
        return StructureResolver.read(typeStructure) + " " + name;
    }

}
