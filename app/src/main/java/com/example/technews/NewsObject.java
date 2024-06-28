package com.example.technews;

public class NewsObject {
    private String imageURl;
    private String title;
    private String author;
    private String date;
    private String description;
    private String url;
    private String content;

    public NewsObject() {
    }

    public NewsObject(String imageURl, String title, String author, String date, String description, String url, String content) {
        this.imageURl = imageURl;
        this.title = title;
        this.author = author;
        this.date = date;
        this.description = description;
        this.url = url;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setImageURl(String imageURl) {
        this.imageURl = imageURl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getImageURl() {
        return imageURl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }
}
