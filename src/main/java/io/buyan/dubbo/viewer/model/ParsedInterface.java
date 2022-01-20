package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 接口模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class ParsedInterface implements Serializable {

    private String className;

    private List<ParsedMethod> parsedMethods = new ArrayList<>();

    public void addParsedMethod(ParsedMethod parsedMethod) {
        parsedMethods.add(parsedMethod);
    }

}
