package com.liao.core.spider;

import com.google.gson.JsonObject;

/**
 * <p>
 * 爬虫解析器
 * </p>
 *
 * @author LiAo
 * @since 2023-08-16
 */
public class SpiderResolver<T> {

    private JsonObject spiderDesc;

    public SpiderResolver(JsonObject jsonObject) {
        this.spiderDesc = jsonObject;
    }

    /**
     * 根据 动作描述文件执行解析
     *
     * @return 解析结果
     */
    public T execute() {
        JsonObject spiderAction;

        if (this.spiderDesc.has("spider-action")) {
            spiderAction = this.spiderDesc.get("spider-action").getAsJsonObject();
        }

        return null;
    }

    public T resolver(JsonObject spiderDesc) {
        if (!spiderDesc.has("element")) {
            return null;
        }

        if (spiderDesc.has("is-leaf")) {
            return null;
        }

        return null;
    }
}
