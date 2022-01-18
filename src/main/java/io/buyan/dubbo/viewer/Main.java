package io.buyan.dubbo.viewer;

import io.buyan.dubbo.viewer.model.ParsedClass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/17
 */
public class Main {

    public static void main(String[] args) throws Exception {
        List<String> path = new ArrayList<>();
        String base = "/Users/brandon.p.gan/Desktop/sdk-storage/";
        path.add("facade-sdk");
        path.add("facade-model");
        path.add("infra-core");
//        path.add("feign");
        path.add("feign-core");
        path.add("common-core");
//        path.add("spring-cloud-context");
//        path.add("spring-web");


        File[] files = path.stream().map(f -> new File(base + f + ".jar")).collect(Collectors.toList()).toArray(new File[]{});

        ApiScanner scanner = new ApiScanner(files, "cn.yzw.iec.auac.facade");

        List<ParsedClass> parsedClasses = scanner.scanApi();
        parsedClasses.forEach(System.out::println);

    }

}
