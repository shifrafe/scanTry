//model of page in creating book recyclerView
package com.example.myapplication.create_book;

import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;
import android.view.View;

public class PageModel {

    private Bitmap bit;
    private MediaRecorder myAudioRecorder;
    private String outputFile;
    //save the state of the buttons
    private int play;
    private int puse;
    private int record;
    //tells if its page with sound, image
    private Boolean haveImage;
    private Boolean haveRecord;

    public PageModel(Bitmap b, int n) {
        this.bit = b;
        this.haveImage = false;
        this.haveRecord = false;

        String page = Integer.toString(n);
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recored" + page + ".3gp";

        record = View.VISIBLE;
        puse = View.INVISIBLE;
        play = View.INVISIBLE;

    }

    public int getPlay() {
        return play;
    }

    public int getPuse() {
        return puse;
    }

    public int getRecord() {
        return record;
    }

    public Boolean getHaveImage() {
        return haveImage;
    }

    public void setHaveImage(Boolean haveImage) {
        this.haveImage = haveImage;
    }

    public Boolean getHaveRecord() {
        return haveRecord;
    }

    public void setHaveRecord(Boolean haveRecord) {
        this.haveRecord = haveRecord;
    }

    public MediaRecorder getMyAudioRecorder() {
        return myAudioRecorder;
    }

    public void setMyAudioRecorder(MediaRecorder myAudioRecorder) {
        this.myAudioRecorder = myAudioRecorder;
    }

    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }

    public Bitmap getBit() {
        return bit;
    }

    public void setBit(Bitmap bit) {
        this.bit = bit;
        haveImage = true;
    }

    //start recored of this page
    public void startRecored() {
        try {

            myAudioRecorder = new MediaRecorder();
            myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            myAudioRecorder.setOutputFile(outputFile);

            myAudioRecorder.prepare();
            myAudioRecorder.start();
            puse = View.VISIBLE;
            play = View.INVISIBLE;
            record = View.INVISIBLE;

            haveRecord = true;
        } catch (Exception e) {
            Log.i("startRecored", "error");
        }

    }

    //stop and save recored of this page
    public void stopRecored() {
        try{
            myAudioRecorder.stop();
            myAudioRecorder.release();

        }catch(RuntimeException stopException){
            //handle cleanup here
        }
        record = View.INVISIBLE;
        puse = View.INVISIBLE;
        play = View.VISIBLE;

        haveRecord = true;

    }

    //play the recored of this page
    public void playRecored() {
        MediaPlayer mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(outputFile);
            mediaPlayer.prepare();
            mediaPlayer.start();
            record = View.INVISIBLE;
            puse = View.INVISIBLE;
            play = View.VISIBLE;

        } catch (Exception e) {
            Log.i("playRecored", "error");
        }
    }

    public void setPlay(int play) {
        this.play = play;
    }

    public void setPuse(int puse) {
        this.puse = puse;
    }

    public void setRecord(int record) {
        this.record = record;
    }
}

