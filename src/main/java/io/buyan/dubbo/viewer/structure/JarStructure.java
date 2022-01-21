package io.buyan.dubbo.viewer.structure;

import lombok.Data;

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
@Data
public class JarStructure implements Serializable {

    private String jarName;

    private List<InterfaceStructure> interfaces;

    private Map<String, String> errors = new HashMap<>();

}
