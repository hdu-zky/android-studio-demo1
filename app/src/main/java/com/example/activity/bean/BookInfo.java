package com.example.activity.bean;

public class BookInfo {
    private int bookId;
    private int pageCount;
    private String bookImg;
    private String bookName;
    private String authorName;
    private String bookTypeName;
    private String status;
    private String updateTime;
    private int latestChapter;
    private String latestChTitle;
    private String Introduction;

    public BookInfo(int bookId, int count, String bookImg, String name, String author, String typeName,
                    String stat, String time, int latestChapter, String latestChTitle, String intro){
        this.bookId = bookId;
        this.pageCount = count;
        this.bookImg = bookImg;
        this.bookName = name;
        this.authorName = author;
        this.bookTypeName = typeName;
        this.status = stat;
        this.updateTime = time;
        this.latestChapter = latestChapter;
        this.latestChTitle = latestChTitle;
        this.Introduction = intro;

    }
    public int getBookId() {
        return bookId;
    }
    public int getPageCount() {
        return pageCount;
    }
    public String getImageSrc() {
        return bookImg;
    }
    public String getBookName() {
        return bookName;
    }
    public String getBookAuthor() {
        return authorName;
    }
    public String getBookTypeName() {
        return bookTypeName;
    }
    public String getStatus() {
        return status;
    }
    public String getUpdateTime() {
        return updateTime;
    }
    public int getLatestChapter() {
        return latestChapter;
    }
    public String getLatestChTitle() {
        return latestChTitle;
    }
    public String getIntroduction() {
        return Introduction;
    }
}
