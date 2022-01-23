package io.buyan.dubbo.viewer.demo.model;

import lombok.Data;

import java.util.List;

/**
 * @author Gan Pengyu
 * CreateDate 2022/1/23
 */
@Data
public class Grade {

    private String name;

    private List<Clazz> classes;

}
