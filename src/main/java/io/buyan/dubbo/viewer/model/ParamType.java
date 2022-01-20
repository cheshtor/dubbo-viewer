package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Gan Pengyu
 * CreateDate 2022/1/20
 */
@Data
public class ParamType implements Serializable {

    private String rawType;

    private List<String> parameterizedTypes = new ArrayList<>();

    public void addParameterizedType(String type) {
        parameterizedTypes.add(type);
    }

}
