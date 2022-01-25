package io.buyan.dubbo.viewer.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jar 包结构
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class JarStructure implements Serializable {

    /**
     * Jar 包名称
     */
    private String jarName;

    /**
     * Jar 包所有接口的抽象结构
     */
    private List<InterfaceStructure> interfaces;

    /**
     * Jar 包解析中的异常
     */
    private Map<String, String> errors = new HashMap<>();

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

    public List<InterfaceStructure> getInterfaces() {
        return interfaces;
    }

    public void setInterfaces(List<InterfaceStructure> interfaces) {
        this.interfaces = interfaces;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
