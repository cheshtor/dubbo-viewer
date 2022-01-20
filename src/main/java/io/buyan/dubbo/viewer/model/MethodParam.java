package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static io.buyan.dubbo.viewer.utils.NameUtil.batchSimplify;
import static io.buyan.dubbo.viewer.utils.NameUtil.simplify;

/**
 * 方法参数模型
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/18
 */
@Data
public class MethodParam implements Serializable {

    private int index;

    private String rawType;

    private String param;

    private String simpleParam;

    private List<String> parameterizedTypes = new ArrayList<>();

    public String getParam() {
        if (parameterizedTypes.isEmpty()) {
            return rawType + " arg" + index;
        }
        String generic = String.join(", ", parameterizedTypes);
        return rawType + "<" + generic + "> arg" + index;
    }

    public String getSimpleParam() {
        if (parameterizedTypes.isEmpty()) {
            return simplify(rawType) + " arg" + index;
        }
        String generic = String.join(", ", batchSimplify(parameterizedTypes));
        return simplify(rawType) + "<" + generic + "> arg" + index;
    }

    public void addParameterizedType(String type) {
        parameterizedTypes.add(type);
    }

}
