package com.rabbit.foot.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * <p>
 * Json 爬虫解析器
 * 用于解析资源类型为Json的爬虫执行器
 * </p>
 *
 * @author LiAo
 * @since 2024-04-10
 */
public class JsonParser<T> extends BaseParser implements Parser<T> {

    /**
     * 爬虫执行
     *
     * @param activeRes 爬虫行为描述
     */
    @Override
    public List<T> execute(JsonNode activeRes) {
        return null;
    }
}
