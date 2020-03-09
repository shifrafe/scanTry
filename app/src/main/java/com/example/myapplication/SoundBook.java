package com.example.myapplication;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.create_book.PageObject;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class SoundBook extends BaseMenu {

    private BookObject book;
    private StorageReference storageRef;
    private ArrayList<MediaPlayer> m_players;
    private MediaPlayer mediaPlayerNextPage, mediaPlayerCurrentPage;
    private int voiceCounter = 0;
    TextView textView;

    private Boolean finishPlayPre = false, finishDownNext = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_book);

        textView = findViewById(R.id.textView);
        this.book = (BookObject) getIntent().getSerializableExtra("book");
        storageRef = FirebaseStorage.getInstance().getReference();

        //resize the players list
        m_players = new ArrayList<>();
        for (int i = 0; i < book.getPageObjects().size(); i++) {
            m_players.add(new MediaPlayer());
        }

        startPlay(book.getPageObjects());
    }
    //-----------------------------------------------------------------------------------

    MediaPlayer.OnPreparedListener preparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
            finishDownNext = true;
            nextPages();
        }
    };

    //-----------------------------------------------------------------------------------
    //start play the book. download the first voice and play it,
    //in parallel download the second voice(if exist), play it only after the first finish
    private void startPlay(final List<PageObject> pages) {

        try {
            //get the current page
            PageObject pageObject = pages.get(voiceCounter);
            //prepare media player for the current and next page if exists
            mediaPlayerCurrentPage = m_players.get(voiceCounter);
            //load the current page audiobook
            downloadPage(mediaPlayerCurrentPage, pageObject);

            //get the next page
            if (voiceCounter + 1 < pages.size()) //only if there is second page //|| nextPageObject!= null
            {
                Log.i("inrange", "page");
                PageObject nextPageObject = pages.get(voiceCounter + 1);
                mediaPlayerNextPage = m_players.get(voiceCounter + 1);
                downloadPage(mediaPlayerNextPage, nextPageObject);
                //load the next page audio source
                mediaPlayerNextPage.setOnPreparedListener(preparedListener);
            }

            mediaPlayerCurrentPage.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //play the current page
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mediaPlayer) {
                            finishPlayPre = true;
                            nextPages();
                        }
                    });

                }
            });
        } catch (Exception e) {
            Log.i("SoundBook", e.getMessage());
            //TODO handle exception
        }
    }


    //---------------------------------------------------------------------------------------
    //going to play next pages only if the previous finish play and the next page ready to play
    private void nextPages() {
        Log.i("isOke1", finishDownNext.toString());
        Log.i("isOke2", finishPlayPre.toString());

        if (finishDownNext && finishPlayPre) {

            finishPlayPre = false;
            finishDownNext = false;
            voiceCounter++;
            startNextPage(m_players.get(voiceCounter));

        } else if (voiceCounter + 1 >= book.getPageObjects().size()) {
            textView.setText("finish book");
            //TODO finish play book
            return;
        }
        return;
    }

    //--------------------------------------------------------------------------------------------
    //currMediaPlayer is after prepare (ready to start)
    private void startNextPage(MediaPlayer currPageMediaPlayer) {

        //play the current page
        currPageMediaPlayer.start();
        currPageMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                finishPlayPre = true;
                nextPages();
            }
        });
        if (voiceCounter + 1 >= book.getPageObjects().size())//voiceCounter+1 = the last page in the book
            return;

        PageObject nextPageObject = book.getPageObjects().get(voiceCounter + 1);
        MediaPlayer nextPageMediaPlayer = m_players.get(voiceCounter + 1);

        downloadPage(nextPageMediaPlayer, nextPageObject);
        //load the next page audio source
        nextPageMediaPlayer.setOnPreparedListener(preparedListener);
    }


    //---------------------------------------------------------------------------------------
    //download and prepare one page in the book
    private void downloadPage(final MediaPlayer player, PageObject pageObject) {
        Log.i("urlpage: getVoice() ", pageObject.getVoice());
        storageRef.child(pageObject.getVoice()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(uri.toString());
                    player.prepare();

                } catch (Exception e) {
                    Log.i("exception:", "sound book");
                    //TODO handle exception
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //TODO Handle any errors
            }
        });
    }
}
