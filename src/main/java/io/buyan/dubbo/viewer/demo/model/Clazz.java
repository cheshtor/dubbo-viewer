package io.buyan.dubbo.viewer.demo.model;

import lombok.Data;

import java.util.List;

/**
 * @author Gan Pengyu
 * CreateDate 2022/1/23
 */
@Data
public class Clazz {

    private String name;

    private HeadMaster headMaster;

    private List<Student> students;

}
