package com.example.activity.bean;

public class BookContent {
    private int bookId;
    private String bookName;
    private int prevChapterId;
    private int bookChapterId;
    private int nextChapterId;
    private String chapterTitle;
    private String bookContent;

    public BookContent(int id, String name, int prevId, int chapterId, int nextId, String chapterTitle, String content){
        this.bookId = id;
        this.bookName = name;
        this.prevChapterId = prevId;
        this.bookChapterId = chapterId;
        this.nextChapterId = nextId;
        this.chapterTitle = chapterTitle;
        this.bookContent = content;

    }

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public int getPrevChapterId() {
        return prevChapterId;
    }

    public int getBookChapterId() {
        return bookChapterId;
    }

    public int getNextChapterId() {
        return nextChapterId;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public String getBookContent() {
        return bookContent;
    }
}
