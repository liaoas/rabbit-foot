package com.rabbit.foot.parser;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * <p>
 * 爬虫解析器抽象类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
public interface Parser<T> {

    /**
     * 爬虫执行
     */
    List<T> execute(JsonNode activeRes);
}
