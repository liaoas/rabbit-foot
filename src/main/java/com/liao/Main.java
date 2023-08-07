package com.liao;

import com.liao.core.ActionResourcesJson;

/**
 * <p>
 * JsonSpiderBuild
 * </p>
 *
 * @author LiAo
 * @since 2023/8/2
 */
public class Main {
    public static void main(String[] args) {

        ActionResourcesJson<String> jsonSpiderBuild = new ActionResourcesJson<>();

        jsonSpiderBuild.readSpiderActionResourceJson();

        System.out.println(jsonSpiderBuild.getRequestCenter());
    }

}