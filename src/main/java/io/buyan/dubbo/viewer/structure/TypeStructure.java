package io.buyan.dubbo.viewer.structure;

import io.buyan.dubbo.viewer.StructureResolver;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;

/**
 * 类型结构定义。对于一个泛型类型声明，如 List<User>，List 称为非泛型部分，而 User 称为泛型部分。
 * 类型结构链的构建参见 {@link StructureResolver#buildTypeStructureChain(io.buyan.dubbo.viewer.structure.TypeStructure)}
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class TypeStructure implements Serializable {

    /**
     * 非泛型部分的类型
     */
    private Type rawType;

    /**
     * 非泛型部分的类型的名称
     */
    private String typeName;

    /**
     * 泛型部分类型
     */
    private List<TypeStructure> genericTypes;

    public Type getRawType() {
        return rawType;
    }

    public void setRawType(Type rawType) {
        this.rawType = rawType;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public List<TypeStructure> getGenericTypes() {
        return genericTypes;
    }

    public void setGenericTypes(List<TypeStructure> genericTypes) {
        this.genericTypes = genericTypes;
    }
}
