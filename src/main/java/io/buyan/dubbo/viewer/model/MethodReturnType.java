package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 方法返回值模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class MethodReturnType {

    private String rawType;

    private List<String> parameterizedTypes = new ArrayList<>();

    public String getReturnType() {
        if (parameterizedTypes.isEmpty()) {
            return rawType;
        }
        String generic = String.join(", ", parameterizedTypes);
        return rawType + "<" + generic + ">";
    }

    public void addParameterizedType(String type) {
        parameterizedTypes.add(type);
    }

}
