package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.model.MethodParam;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public class Test {

    public static void main(String[] args) throws Exception {
//        Class<Hello> clazz = Hello.class;
        Class<Model> clazz = Model.class;

        Method method = clazz.getDeclaredMethod("get", List.class, Map.class, Set.class);
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        List<MethodParam> methodParams = new ArrayList<>();
        int argIndex = 0;
        for (Type type : genericParameterTypes) {
            MethodParam param = new MethodParam();
            param.setIndex(argIndex++);
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                param.setRawType(parameterizedType.getRawType().getTypeName());
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type at : actualTypeArguments) {
                    param.addParameterizedType(at.getTypeName());
//                    System.out.println(at.getTypeName());
                }
            } else {
//                System.out.println(type.getTypeName());
                param.setRawType(type.getTypeName());
            }
            methodParams.add(param);
        }
        String list = methodParams.stream().map(MethodParam::getParam).collect(Collectors.joining(", "));
        System.out.println("（" + list + "）");
    }

}
