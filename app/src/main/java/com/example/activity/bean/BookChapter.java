package com.example.activity.bean;

public class BookChapter {
    private int bookId;
    private String bookChapterId;
    private String chapterTitle;
    public BookChapter(int bookId, String bookChapterId, String chapterTitle){
        this.bookId = bookId;
        this.bookChapterId = bookChapterId;
        this.chapterTitle = chapterTitle;
    }
    public int getBookId(){
        return bookId;
    }
    public String getBookChapterId(){
        return bookChapterId;
    }
    public String getChapterTitle(){
        return chapterTitle;
    }
}
