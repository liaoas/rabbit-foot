package com.liao.core.spider;

import java.util.List;

/**
 * <p>
 * 爬虫解析器抽象类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-28
 */
public interface Resolver<T> {

    /**
     * 爬虫执行
     */
    List<T> execute(String spiderName,String... params);
}
