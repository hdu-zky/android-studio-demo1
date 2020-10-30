package com.example.activity.bean;

public class BookShelf {
    private String imageSrc;
    private String bookName;
    private String bookAuthor;
    private String bookTypeName;
    private int bookId;
    private int status;
    private String updateTime;
    private String latestChapter;
    private String latestChTitle;

    public BookShelf(String imageSrc, String name, String author, String typeName,int bookId,
                     String time, int stat, String latestChapter, String latestChTitle){
        this.imageSrc = imageSrc;
        this.bookName = name;
        this.bookAuthor = author;
        this.bookTypeName = typeName;
        this.bookId = bookId;
        this.updateTime = time;
        this.status = stat;
        this.latestChapter = latestChapter;
        this.latestChTitle = latestChTitle;

    }

    public String getImageSrc() {
        return imageSrc;
    }
    public String getBookName() {
        return bookName;
    }
    public String getBookAuthor() {
        return bookAuthor;
    }
    public String getBookTypeName() {
        return bookTypeName;
    }
    public int getBookId() {
        return bookId;
    }
    public String getUpdateTime() {
        return updateTime;
    }
    public int getStatus() {
        return status;
    }
    public String getLatestChapter() {
        return latestChapter;
    }
    public String getLatestChTitle() {
        return latestChTitle;
    }
}
