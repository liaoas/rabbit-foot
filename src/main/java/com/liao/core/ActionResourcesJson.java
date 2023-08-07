package com.liao.core;

import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.util.ObjUtil;
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
 * 爬虫构造器
 * </p>
 *
 * @author LiAo
 * @since 2023-08-03
 */
@Slf4j
public class ActionResourcesJson<T> {

    private JsonObject action;

    public ActionResourcesJson() {
    }

    public T getRequestCenter() {

        JsonArray booksArray = this.action.get("books").getAsJsonArray();

        for (JsonElement bookElement : booksArray) {
            JsonObject bookObj = bookElement.getAsJsonObject();
            JsonObject siteObj = bookObj.get("site").getAsJsonObject();
            HttpAsk<T> httpAsk = new HttpAsk<>(siteObj);
            return httpAsk.execute();
        }

        return null;
    }


    /**
     * 读取爬虫动作的资源文件
     */
    public void readSpiderActionResourceJson() {

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
            this.action = gson.fromJson(sb, JsonObject.class);
        }
    }

}
