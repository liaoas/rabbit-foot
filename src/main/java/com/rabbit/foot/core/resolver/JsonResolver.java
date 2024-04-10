package com.rabbit.foot.core.resolver;

import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * <p>
 * Json 爬虫解析器
 * </p>
 *
 * @author LiAo
 * @since 2024-04-10
 */
public class JsonResolver<T> extends BaseResolver implements Resolver<T> {
    
    /**
     * 爬虫执行
     *
     * @param activeRes 爬虫动作描述
     */
    @Override
    public List<T> execute(JsonNode activeRes) {
        return null;
    }
}
