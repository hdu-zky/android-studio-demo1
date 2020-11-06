package com.example.activity.bean;

public class BookSearch {
    private int bookId;
    private String bookName;
    private String authorName;
    private String bookTypeName;
    private String status;
    public BookSearch(int bookId, String bookName, String authorName,String bookTypeName,String status){
        this.bookId = bookId;
        this.bookName = bookName;
        this.authorName = authorName;
        this.bookTypeName = bookTypeName;
        this.status = status;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public String getAuthorName() {
        return authorName;
    }

    public String getBookTypeName() {
        return bookTypeName;
    }

    public String getStatus() {
        return status;
    }
}
