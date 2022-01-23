package io.buyan.dubbo.viewer.demo;

import io.buyan.dubbo.viewer.structure.TypeStructure;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import static io.buyan.dubbo.viewer.StructureResolver.buildTypeStructure;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class Test {

    public static void main(String[] args) throws Exception {
        Method method = Demo.class.getMethod("c", List.class);
        Type[] genericParameterTypes = method.getGenericParameterTypes();
        TypeStructure structure = new TypeStructure();
        structure.setRawType(genericParameterTypes[0]);
        structure.setTypeName(genericParameterTypes[0].getTypeName());


        TypeStructure parsed = buildTypeStructure(structure);
//        System.out.println(JSON.toJSONString(parsed));
//        readProperty(parsed);
    }

}
