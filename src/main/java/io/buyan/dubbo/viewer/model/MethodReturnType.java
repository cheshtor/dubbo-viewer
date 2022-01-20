package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static io.buyan.dubbo.viewer.utils.NameUtil.batchSimplify;
import static io.buyan.dubbo.viewer.utils.NameUtil.simplify;

/**
 * 方法返回值模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class MethodReturnType implements Serializable {

    private String rawType;

    private List<String> parameterizedTypes = new ArrayList<>();

    private String returnType;

    private String simpleReturnTye;

    public String getReturnType() {
        if (parameterizedTypes.isEmpty()) {
            return rawType;
        }
        String generic = String.join(", ", parameterizedTypes);
        return rawType + "<" + generic + ">";
    }

    public String getSimpleReturnType() {
        if (parameterizedTypes.isEmpty()) {
            return simplify(rawType);
        }
        String generic = String.join(", ", batchSimplify(parameterizedTypes));
        return simplify(rawType) + "<" + generic + ">";
    }

    public void addParameterizedType(String type) {
        parameterizedTypes.add(type);
    }

}
