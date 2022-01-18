package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class ParsedClass {

    private String className;

    private List<ParsedMethod> parsedMethods = new ArrayList<>();

    public void addParsedMethod(ParsedMethod parsedMethod) {
        parsedMethods.add(parsedMethod);
    }

}
