package com.liao.entity;

/**
 * <p>
 * 结果实体类，用于测试
 * </p>
 *
 * @author LiAo
 * @since 2023-09-18
 */
public class ResultEntity {

    private String title;

    private String url;

    public ResultEntity() {
    }

    public ResultEntity(String title, String url) {
        this.title = title;
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
