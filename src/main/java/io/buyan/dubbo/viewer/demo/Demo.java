package io.buyan.dubbo.viewer.demo;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public interface Demo {

    List<Map<String, Map<Long, Set<List<Map<String, Integer>>>>>> a(List<Map<String, Map<Long, Set<List<Map<String, Integer>>>>>> x);

    String b();

    // Map<String, Map<Long, Set<List<Map<String, Integer>>>>>
    // Map<Long, Set<List<Map<String, Integer>>>>

}
