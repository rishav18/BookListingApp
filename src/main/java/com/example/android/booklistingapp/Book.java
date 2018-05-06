package com.example.android.booklistingapp;

public class Book {
    private String author;
    private String title;
    private String Url;
    private String image;

    public Book(String bimage, String bauthor, String btitle, String burl) {
        image = bimage;
        author = bauthor;
        title = btitle;
        Url = burl;
    }

    public String getAuthor() {  return author; }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return Url;
    }

    public String getImage() {
        return image;
    }
}
