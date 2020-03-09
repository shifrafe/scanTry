//class of item page of book that save in the firebase
package com.example.myapplication.create_book;

import java.io.Serializable;

public class PageObject  implements Serializable {
    private String image;
    private String voice;

    public PageObject(String image, String voice) {
        this.image = image;
        this.voice = voice;
    }

    public PageObject() {
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVoice() {
        return voice;
    }

    public void setVoice(String voice) {
        this.voice = voice;
    }
}
