package io.buyan.dubbo.viewer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public interface Hello {

    List<Map<String, String>> go();

    String get();

    <S> S demo(Class<S> s);

    String find(String a, List<Integer> b, Map<String, List<Map<String, Set<Long>>>> c);

}
