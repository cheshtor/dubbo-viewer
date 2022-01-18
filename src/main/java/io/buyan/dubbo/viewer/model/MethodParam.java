package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法参数模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class MethodParam {

    private int index;

    private String rawType;

    private List<String> parameterizedTypes = new ArrayList<>();

    public String getParam() {
        if (parameterizedTypes.isEmpty()) {
            return rawType + " arg" + index;
        }
        String generic = String.join(", ", parameterizedTypes);
        return rawType + "<" + generic + "> arg" + index;
    }

    public void addParameterizedType(String type) {
        parameterizedTypes.add(type);
    }

}
