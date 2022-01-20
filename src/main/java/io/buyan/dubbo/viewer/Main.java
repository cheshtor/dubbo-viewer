package io.buyan.dubbo.viewer;

import com.alibaba.fastjson.JSON;
import io.buyan.dubbo.viewer.model.ParsedResult;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Pengyu Gan
 * CreateDate 2022/1/19
 */
public class Main {

    public static void main(String[] args) {
        String basePath = "lib/";
        List<String> filenames = new ArrayList<>();
        filenames.add("facade-sdk.jar");
        filenames.add("facade-model.jar");
        filenames.add("infra-core.jar");
        filenames.add("common-core.jar");
        filenames.add("feign-core.jar");

        String[] basePackages = {"cn.yzw.iec.auac.facade.sdk"};

        File[] files = filenames.stream().map(name -> new File(basePath + name)).collect(Collectors.toList()).toArray(new File[]{});


        ApiScanner apiScanner = new ApiScanner(files, basePackages);

        ParsedResult parsedResult = apiScanner.scanApi();
        System.out.println(JSON.toJSON(parsedResult));

    }

}
