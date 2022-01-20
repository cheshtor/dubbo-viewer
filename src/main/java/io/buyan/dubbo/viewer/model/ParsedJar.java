package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Jar 包模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/19
 */
@Data
public class ParsedJar implements Serializable {

    private String jarName;

    private List<ParsedInterface> parsedInterfaces = new ArrayList<>();

    private List<String> canNotLoadClass = new ArrayList<>();

    private List<String> noMethodClass = new ArrayList<>();

//    private List<String> canNotFindClass = new ArrayList<>();

    private Map<String, String> canNotFindClass = new HashMap<>();

    public void addParsedInterface(ParsedInterface parsedInterface) {
        parsedInterfaces.add(parsedInterface);
    }

}
