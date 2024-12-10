package com.rabbit.foot.parser;

import com.rabbit.foot.loader.ActionResources;
import org.jsoup.nodes.Document;

/**
 * <p>
 * 爬虫基础类
 * </p>
 *
 * @author LiAo
 * @since 2023-09-20
 */
public abstract class BaseParser extends ActionResources {

    /**
     * 爬取的Web节点
     */
    protected Document webDocument;
}
