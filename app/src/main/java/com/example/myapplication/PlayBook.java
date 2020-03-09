package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;


public class PlayBook extends BaseMenu {
    private TextView name;
    private TextView author;
    private ImageButton read;
    private ImageButton sounds;
    private ImageButton audio;
    private BookObject book;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_book);
        name = findViewById(R.id.pname_book);
        author = findViewById(R.id.pauthor);
        read = findViewById(R.id.readBook);
        sounds = findViewById(R.id.soundBook);
        audio = findViewById(R.id.audioBook);

        this.book = (BookObject)getIntent().getSerializableExtra("book");

        name.setText(book.getName());
        author.setText(book.getAuthor());
        addListenerBut();
        avilableButtons();
    }
    private void addListenerBut(){
        sounds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayBook.this, SoundBook.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });

        read.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PlayBook.this, ReadBook.class);
                intent.putExtra("book", book);
                startActivity(intent);
            }
        });
    }

    private void avilableButtons(){
        if(!book.isHaveImages())
        {
            read.setEnabled(false);
            read.setImageAlpha(25);
            audio.setEnabled(false);
            audio.setImageAlpha(25);

        }

        if(!book.isHaveRecoreds())
        {
            sounds.setEnabled(false);
            sounds.setImageAlpha(25);
            audio.setEnabled(false);
            audio.setImageAlpha(25);

        }
    }
}
