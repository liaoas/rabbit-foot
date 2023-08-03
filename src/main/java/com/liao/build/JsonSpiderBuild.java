package com.liao.build;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * <p>
 * 爬虫构造器
 * </p>
 *
 * @author LiAo
 * @since 2023-08-03
 */
public class JsonSpiderBuild<T> {

    private T t;

    private JsonObject gson;

    public JsonSpiderBuild(JsonObject gson) {
        this.gson = gson;
    }


    public T grab() {
        return (T) gson.toString();
    }

}
