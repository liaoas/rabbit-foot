package com.rabbit.foot.entity;

import lombok.Data;
import lombok.Getter;

/**
 * <p>
 *
 * </p>
 *
 * @author LiAo
 * @since 2024-01-15
 */
public class Chapter {

    // 链接
    private String link;

    // 名称
    private String name;

    public void setLink(String link) {
        this.link = link;
    }

    public void setName(String name) {
        this.name = name;
    }
}
