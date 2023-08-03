package com.liao;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.liao.build.JsonSpiderBuild;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Map;

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


        JsonObject gson = getResourceActiveJson();

        if (gson == null) {
            return;
        }

        JsonSpiderBuild<String> jsonSpiderBuild = new JsonSpiderBuild<>(gson);

        System.out.println(jsonSpiderBuild.grab());
    }

    public static JsonObject getResourceActiveJson() {

        // 通过 ClassLoader 获取资源文件的输入流
        InputStream inputStream = Main.class.getClassLoader().getResourceAsStream("spider-action-test.json");

        StringBuilder sb = new StringBuilder();
        // 使用 inputStream 来读取文件内容
        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Resource not found!");
        }

        if (sb.length() != 0) {

            Gson gson = new Gson();
            return gson.fromJson(sb.toString(), JsonObject.class);
        }

        return null;
    }
}