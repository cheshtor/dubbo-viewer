package io.buyan.dubbo.viewer.structure;

import lombok.Data;

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
@Data
public class Result implements Serializable {

    private List<JarStructure> jars;

    private Map<String, String> fileTranslateError = new HashMap<>();

    private Map<String, Map<String, String>> beanProperty = new HashMap<>();

}
