package io.buyan.dubbo.viewer.structure;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 接口结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
@Data
public class InterfaceStructure implements Serializable {

    private String classname;

    private List<MethodStructure> methods;

}
