package com.rabbit.foot.common.enums;

/**
 * <p>
 * 爬虫资源类型
 * </p>
 *
 * @author LiAo
 * @since 2024-04-08
 */
public enum ReptileType {

    // 搜索
    SEARCH("search"),

    // 章节
    CHAPTER("chapter"),

    // 书籍
    CONTENT("content"),
    ;

    public final String value;

    ReptileType(String value) {
        this.value = value;
    }
}
