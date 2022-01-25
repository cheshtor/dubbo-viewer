package io.buyan.dubbo.viewer.structure;

import java.io.Serializable;
import java.util.List;

/**
 * 接口结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class InterfaceStructure implements Serializable {

    /**
     * 接口类名
     */
    private String classname;

    /**
     * 接口方法
     */
    private List<MethodStructure> methods;

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public List<MethodStructure> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodStructure> methods) {
        this.methods = methods;
    }
}
