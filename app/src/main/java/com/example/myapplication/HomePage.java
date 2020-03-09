package com.example.myapplication;
//the user HomePage in
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.book_library.BookLibrary;
import com.example.myapplication.create_book.CreateBookActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class HomePage extends BaseMenu {

    TextView user;
    FirebaseDatabase database;
    DatabaseReference ref;
    ImageButton btnCBook, bthLibrary;
    FirebaseAuth mAuth;
    String currUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        bthLibrary = findViewById(R.id.btLibrary);
        btnCBook = findViewById(R.id.btnCreatBook);
        user = findViewById(R.id.user);
        currUserId = mAuth.getCurrentUser().getUid();

        btnCBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(currUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        user.setText(mAuth.getCurrentUser().getDisplayName());
                        Intent intent = new Intent(HomePage.this, CreateBookActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });

        bthLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(currUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Intent intent = new Intent(HomePage.this, BookLibrary.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }
        });
    }
}