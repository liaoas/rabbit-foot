package com.rabbit.foot.entity;

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

    public Chapter() {
    }

    public Chapter(String link, String name) {
        this.link = link;
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
