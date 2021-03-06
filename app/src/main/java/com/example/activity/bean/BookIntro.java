package com.example.activity.bean;

import android.util.Log;

public class BookIntro {
    private String imageSrc;
    private String bookName;
    private String bookAuthor;
    private String bookTypeName;
    private int bookId;
    private String bookIntroduction;

    public BookIntro(String imageSrc, String name, String author, String typeName,int id, String intro){
        this.imageSrc = imageSrc;
        this.bookName = name;
        this.bookAuthor = author;
        this.bookTypeName = typeName;
        this.bookId = id;
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
    public String getBookTypeName() {
        return bookTypeName;
    }
    public int getBookId() {
        return bookId;
    }
    public String getBookIntroduction() {
        return bookIntroduction;
    }

}
