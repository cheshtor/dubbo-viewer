package io.buyan.dubbo.viewer.demo;

import io.buyan.dubbo.viewer.demo.model.Clazz;
import io.buyan.dubbo.viewer.demo.model.Grade;
import io.buyan.dubbo.viewer.demo.model.HeadMaster;
import io.buyan.dubbo.viewer.demo.model.Student;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/21
 */
public interface Demo {

    List<Map<String, Map<Long, Set<List<Map<String, Integer>>>>>> a(List<Map<String, Map<Long, Set<List<Map<String, Integer>>>>>> x);

    String c(List<Map<Grade, Map<HeadMaster, Set<Map<Student, Clazz>>>>> x);

    String b();

    // Map<String, Map<Long, Set<List<Map<String, Integer>>>>>
    // Map<Long, Set<List<Map<String, Integer>>>>

}
