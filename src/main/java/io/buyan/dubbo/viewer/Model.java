package io.buyan.dubbo.viewer;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public interface Model<X, Y, Z> {

    String get(List<X> a, Map<String, List<Y>> b, Set<Map<X, List<Z>>> c);

}
