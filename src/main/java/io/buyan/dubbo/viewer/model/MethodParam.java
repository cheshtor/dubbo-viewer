package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;

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

    private ParamType paramType;

    private String param;

    private String simpleParam;

    public String getParam() {
        if (paramType.getParameterizedTypes().isEmpty()) {
            return paramType.getRawType() + " arg" + index;
        }
        String generic = String.join(", ", paramType.getParameterizedTypes());
        return paramType.getRawType() + "<" + generic + "> arg" + index;
    }

    public String getSimpleParam() {
        if (paramType.getParameterizedTypes().isEmpty()) {
            return simplify(paramType.getRawType()) + " arg" + index;
        }
        String generic = String.join(", ", batchSimplify(paramType.getParameterizedTypes()));
        return simplify(paramType.getRawType()) + "<" + generic + "> arg" + index;
    }

}
