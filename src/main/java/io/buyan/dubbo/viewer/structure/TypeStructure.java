package io.buyan.dubbo.viewer.structure;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * 类型结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
@Data
public class TypeStructure implements Serializable {

    @JSONField(serialize = false)
    private Type rawType;

    private String typeName;

    private List<TypeStructure> genericTypes;

}
