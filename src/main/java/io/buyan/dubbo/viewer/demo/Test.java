package io.buyan.dubbo.viewer.demo;

import com.alibaba.fastjson.JSON;
import io.buyan.dubbo.viewer.structure.StructureReader;
import io.buyan.dubbo.viewer.structure.TypeStructure;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static io.buyan.dubbo.viewer.utils.NameUtil.simplify;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Method method = Demo.class.getMethod("a", List.class);
        Type type = method.getGenericReturnType();
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        TypeStructure structure = new TypeStructure();
        structure.setRawType(genericParameterTypes[0]);
        structure.setTypeName(genericParameterTypes[0].getTypeName());


        TypeStructure parsed = parse(structure);
        System.out.println(JSON.toJSONString(parsed));

        StructureReader reader = new StructureReader();
        reader.read(parsed);

        LinkedList<String> queue = reader.getQueue();
        StringBuilder stringBuilder = new StringBuilder();
        while (!queue.isEmpty()) {
            String item = queue.poll();
            if (item.contains("<")) {
                queue.offer(">");
            }
            // 取出但不删除下一个元素
            String next = queue.peek();
            if (">".equals(next) && !item.equals(">")) {
                item = item.substring(0, item.indexOf(","));
            }
            stringBuilder.append(simplify(item));
        }
        System.out.println(stringBuilder);
    }

    private static TypeStructure parse(TypeStructure structure) {
        if (null == structure) {
            return null;
        }
        List<TypeStructure> subTypes = structure.getGenericTypes();
        if (null == subTypes || subTypes.isEmpty()) {
            subTypes = new ArrayList<>();
            structure.setGenericTypes(subTypes);
            Type type = structure.getRawType();
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                structure.setRawType(parameterizedType.getRawType());
                structure.setTypeName(parameterizedType.getRawType().getTypeName());
                Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
                for (Type actualTypeArgument : actualTypeArguments) {
                    TypeStructure s = new TypeStructure();
                    s.setRawType(actualTypeArgument);
                    s.setTypeName(actualTypeArgument.getTypeName());
                    subTypes.add(s);
                }
                parse(structure);
            }
        } else {
            for (TypeStructure subType : subTypes) {
                parse(subType);
            }
        }
        return structure;
    }

}
