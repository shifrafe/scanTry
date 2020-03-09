//class of item book that save into the firebase
package com.example.myapplication;

import com.example.myapplication.create_book.PageObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BookObject implements Serializable {
    private String id;
    private String name;
    private String author;
    private boolean haveImages;
    private boolean haveRecoreds;
    private boolean savePablic;
    private List<PageObject> pageObjects;

    public BookObject( String name, String author, boolean savePablic) {
        this.name = name;
        this.author = author;
        this.savePablic = savePablic;
        this.haveImages = false;
        this.haveRecoreds = false;
        pageObjects = new ArrayList<>();
    }

    public BookObject(){

    }
    public void addPage(String image, String voice){
        pageObjects.add(new PageObject(image,voice));

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public boolean isHaveImages() {
        return haveImages;
    }

    public void setHaveImages(boolean haveImages) {
        this.haveImages = haveImages;
    }

    public boolean isHaveRecoreds() {
        return haveRecoreds;
    }

    public void setHaveRecoreds(boolean haveRecoreds) {
        this.haveRecoreds = haveRecoreds;
    }

    public boolean isSavePablic() {
        return savePablic;
    }

    public void setSavePablic(boolean savePablic) {
        this.savePablic = savePablic;
    }

    public List<PageObject> getPageObjects() {
        return pageObjects;
    }

    public void setPageObjects(List<PageObject> pageObjects) {
        this.pageObjects = pageObjects;
    }
}
