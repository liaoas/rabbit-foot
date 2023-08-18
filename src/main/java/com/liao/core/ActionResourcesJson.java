package com.liao.core;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.liao.Main;
import com.liao.network.HttpAsk;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.List;

/**
 * <p>
 * 爬虫动作描述资源类
 * </p>
 *
 * @author LiAo
 * @since 2023-08-03
 */
@Slf4j
public class ActionResourcesJson {

    /**
     * 获取指定名称的爬虫描述
     *
     * @param name 爬虫名称
     * @return 爬虫描述 Json
     */
    public static JsonObject getSpiderActionJson(String name) {
        if (StrUtil.isEmpty(name)) {
            return null;
        }

        JsonObject jsonObject = readSpiderActionJson();

        if (ObjUtil.isNull(jsonObject)) {
            return null;
        }

        JsonArray books = jsonObject.get("books").getAsJsonArray();

        // 获取执行名称的动作
        for (JsonElement bookElement : books) {
            JsonObject bookObj = bookElement.getAsJsonObject();
            if (name.equals(bookObj.get("name").getAsString())) {
                return bookObj;
            }
        }

        return null;
    }

    /**
     * 读取爬虫动作的资源文件
     */
    private static JsonObject readSpiderActionJson() {

        // 通过 ClassLoader 获取资源文件的输入流
        URL resource = Main.class.getClassLoader().getResource("spider-action-test.json");

        if (ObjUtil.isNull(resource)) {
            throw new IllegalArgumentException("爬虫动作描述资源缺失");
        }

        FileReader fileReader = new FileReader(new File(resource.getFile()));

        List<String> strings = fileReader.readLines();

        String sb = String.join("", strings);

        if (!sb.isEmpty()) {
            Gson gson = new Gson();
            return gson.fromJson(sb, JsonObject.class);
        }

        return null;
    }

}
