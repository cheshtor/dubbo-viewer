package io.buyan.dubbo.viewer.model;

import lombok.Data;

import java.io.Serializable;

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

    private ParamType paramType;

    private String returnType;

    private String simpleReturnTye;

    public String getReturnType() {
        if (paramType.getParameterizedTypes().isEmpty()) {
            return paramType.getRawType();
        }
        String generic = String.join(", ", paramType.getParameterizedTypes());
        return paramType.getRawType() + "<" + generic + ">";
    }

    public String getSimpleReturnType() {
        if (paramType.getParameterizedTypes().isEmpty()) {
            return simplify(paramType.getRawType());
        }
        String generic = String.join(", ", batchSimplify(paramType.getParameterizedTypes()));
        return simplify(paramType.getRawType()) + "<" + generic + ">";
    }

}
