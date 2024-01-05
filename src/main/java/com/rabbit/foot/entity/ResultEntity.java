package com.rabbit.foot.entity;

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

    private String author;

    private String newChapter;

    public ResultEntity() {
    }

    public ResultEntity(String title, String url, String author, String newChapter) {
        this.title = title;
        this.url = url;
        this.author = author;
        this.newChapter = newChapter;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setNewChapter(String newChapter) {
        this.newChapter = newChapter;
    }
}
