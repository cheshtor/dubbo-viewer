package io.buyan.dubbo.viewer.structure;

import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public class StructureReader {

    @Getter
    private LinkedList<String> queue = new LinkedList<>();

    public void read(TypeStructure typeStructure) {
        String rawName = typeStructure.getTypeName();
        List<TypeStructure> genericTypes = typeStructure.getGenericTypes();
        if (!genericTypes.isEmpty()) {
            queue.offer(rawName + "<");
            for (TypeStructure genericType : genericTypes) {
                read(genericType);
            }
        } else {
            queue.offer(rawName + ", ");
        }
    }

}
