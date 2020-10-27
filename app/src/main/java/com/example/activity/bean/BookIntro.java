package com.example.activity.bean;

public class BookIntro {
    private String imageSrc;
    private String bookName;
    private String bookAuthor;
    private String bookStatus;
    private String bookIntroduction;

    public BookIntro(String imageSrc, String name, String author, String status, String intro){
        this.imageSrc = imageSrc;
        this.bookName = name;
        this.bookAuthor = author;
        this.bookStatus = status;
        this.bookIntroduction = intro;

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
    public String getBookStatus() {
        return bookStatus;
    }
    public String getBookIntroduction() {
        return bookIntroduction;
    }

}
