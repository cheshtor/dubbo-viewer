package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Api 解析结果
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/19
 */
@Data
public class ParsedResult implements Serializable {

    private Map<String, String> fileTranslateError = new HashMap<>();

    private List<ParsedJar> parsedJars = new ArrayList<>();

    public void addParsedJar(ParsedJar parsedJar) {
        parsedJars.add(parsedJar);
    }

}
