package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.structure.TypeStructure;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static io.buyan.dubbo.viewer.utils.NameUtil.simplify;

/**
 *
 *
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class StructureResolver {

    /**
     * 构造类型声明结构链。对于一个泛型类型声明，如 List<User>，List 称为非泛型部分，而 User 称为泛型部分。
     * 例如存在一个非常复杂的类型声明 List<Map<String, Map<Long, Set<List<Map<String, Integer>>>>>>，
     * 第一次调用本方法时传入的入参 structure，其 rawType 属性保存的就是 List，genericTypes 属性为空。
     * 由于 List 是一个泛型类型，所以可以转换为 ParameterizedType 后进行泛型分析。这样再下一次递归调用时，
     * rawType 的值就是 Map，而 genericTypes 的值就是 String 和 Map。这样层层解析下去，就可以得到一个
     * 完整的泛型类型声明结构链。
     *
     * @param structure 类型声明的最外层结构
     * @return 类型声明结构链
     */
    public static TypeStructure buildTypeStructure(TypeStructure structure) {
        if (null == structure) {
            return null;
        }
        List<TypeStructure> genericTypes = structure.getGenericTypes();
        // 如果 genericTypes 为空，则表示非泛型部分还没有解析
        if (null == genericTypes || genericTypes.isEmpty()) {
            // 每次解析非泛型部分都要为泛型部分（genericTypes）创建一个集合，然后
            // 调用 setGenericTypes 方法进行关联。这样才能把整个类型声明的结构树串起来。
            genericTypes = new ArrayList<>();
            structure.setGenericTypes(genericTypes);
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
                    genericTypes.add(s);
                }
                buildTypeStructure(structure);
            }
        } else { // 如果 genericTypes 不为空，表示非泛型部分已经解析过了，这时候就需要解析泛型部分
            for (TypeStructure subType : genericTypes) {
                buildTypeStructure(subType);
            }
        }
        return structure;
    }

    /**
     * 读取 TypeStructure 还原出字符串形式的类型声明
     * @param typeStructure 类型结构
     * @return 字符串形式的类型声明
     */
    public static String read(TypeStructure typeStructure) {
        LinkedList<String> queue = new LinkedList<>();
        recursion(typeStructure, queue);
        StringBuilder sb = new StringBuilder();
        while (!queue.isEmpty()) {
            String item = queue.poll();
            if (item.contains("<")) {
                queue.offer(">");
            }
            // 取出但不删除下一个元素
            String next = queue.peek();
            /*
                当前元素不为 >
                    下一个元素是 >，表示这是最后一层泛型的最后一个参数
                    没有下一个元素，表示 TypeStructure 不是一个泛型类型
             */
            if (!item.equals(">") && (">".equals(next) || null == next)) {
                item = item.substring(0, item.indexOf(","));
            }
            sb.append(simplify(item));
        }
        return sb.toString();
    }

    public static void readBean(TypeStructure typeStructure, List<String> beanNames) {
        LinkedList<String> queue = new LinkedList<>();
        recursion(typeStructure, queue);
        while (!queue.isEmpty()) {
            String item = queue.poll();
            if (item.contains("<")) {
                item = item.substring(0, item.indexOf("<"));
            }
            if (item.contains(", ")) {
                item = item.substring(0, item.indexOf(","));
            }
            if (!item.startsWith("java.")) {
                beanNames.add(item);
            }
        }
    }

    /**
     * 递归解析 TypeStructure，将每一层的类型放入队列
     * @param typeStructure 类型结构
     * @param queue 队列
     */
    private static void recursion(TypeStructure typeStructure, LinkedList<String> queue) {
        String rawName = typeStructure.getTypeName();
        List<TypeStructure> genericTypes = typeStructure.getGenericTypes();
        if (!genericTypes.isEmpty()) {
            queue.offer(rawName + "<");
            for (TypeStructure genericType : genericTypes) {
                recursion(genericType, queue);
            }
        } else {
            queue.offer(rawName + ", ");
        }
    }

}