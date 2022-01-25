package io.buyan.dubbo.viewer.structure;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jar 包解析结果
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class Result implements Serializable {

    /**
     * 扫描的所有 Jar 包的结构
     */
    private List<JarStructure> jars;

    /**
     * 扫描过程中的错误信息
     */
    private Map<String, String> globalError = new HashMap<>();

    /**
     * 所有方法入参中自定义类型的字段定义
     */
    private Map<String, Map<String, String>> beanProperty = new HashMap<>();

    public List<JarStructure> getJars() {
        return jars;
    }

    public void setJars(List<JarStructure> jars) {
        this.jars = jars;
    }

    public Map<String, String> getGlobalError() {
        return globalError;
    }

    public void setGlobalError(Map<String, String> globalError) {
        this.globalError = globalError;
    }

    public Map<String, Map<String, String>> getBeanProperty() {
        return beanProperty;
    }

    public void setBeanProperty(Map<String, Map<String, String>> beanProperty) {
        this.beanProperty = beanProperty;
    }
}
