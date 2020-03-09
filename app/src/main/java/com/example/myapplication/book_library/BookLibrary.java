package com.example.myapplication.book_library;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BaseMenu;
import com.example.myapplication.BookObject;
import com.example.myapplication.PlayBook;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookLibrary extends BaseMenu implements BookAdapter.OnBookListener {

    private CheckBox showSound;
    private CheckBox showImage;
    private CheckBox showPrivate;
    private CheckBox showPublic;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BookAdapter mAdapter;
    private List<BookObject> myDataset;
    private DatabaseReference publicBooksReference;
    private DatabaseReference privateBooksReference;
    private DatabaseReference booksReference;
    private Set<String> booksRefs = new HashSet<>();
    private boolean isBusy;
    private ProgressDialog loading;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_library);

        showSound = findViewById(R.id.show_sounds);
        recyclerView = findViewById(R.id.recycler_book_library);
        showImage = findViewById(R.id.show_images);
        showPrivate = findViewById(R.id.show_private);
        showPublic = findViewById(R.id.show_public);
        isBusy = false;
        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setTitle("Updating...");
        publicBooksReference = FirebaseDatabase.getInstance().getReference("public_books");
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        privateBooksReference = FirebaseDatabase.getInstance().getReference("Users/" + userId + "/my_books");
        booksReference = FirebaseDatabase.getInstance().getReference("books");
        recyclerView.setHasFixedSize(true);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        int spanCount = 2; // 3 columns
        int spacing = 50; // 50px
        boolean includeEdge = true;
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));

        showPublic.setChecked(true);
        showPrivate.setChecked(true);

        myDataset = new ArrayList<BookObject>();
        addList();
        mAdapter = new BookAdapter(BookLibrary.this, myDataset, this);
        recyclerView.setAdapter(mAdapter);


        setLitener(showPublic);
        setLitener(showPrivate);
        setLitener(showImage);
        setLitener(showSound);


    }

    public void setLitener(CheckBox c) {
        c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                         @Override
                                         public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                             changeStatus();
                                         }
                                     }
        );
    }

    public void addList() {

        loading.show();
        Log.i("loading", "show");

        if (showPrivate.isChecked())
            downloadBooks(privateBooksReference);

        if (showPublic.isChecked())
            downloadBooks(publicBooksReference);
    }

    private void getBooksFromDB() {
        try {
            loading.show();
            int i = 0;
            final int size = booksRefs.size();
            for (final String bookRef : booksRefs) {
                i++;
                final int finalI = i;
                booksReference.child(bookRef).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            BookObject bookObject = dataSnapshot.getValue(BookObject.class);
                            if (checksBox(bookObject)) {
                                myDataset.add(bookObject);
                                mAdapter.notifyDataSetChanged();
                                if (finalI == size) {
                                    loading.dismiss();
                                }
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
            booksRefs.clear();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Boolean checksBox(BookObject b) {

        if (showImage.isChecked() == true)
            if (b.isHaveImages() != true)
                return false;

        if (showSound.isChecked() == true)
            if (b.isHaveRecoreds() != true)
                return false;

        //check if this book is already exist
        for (BookObject bo : myDataset)
            if (b.getId().equals(bo.getId()))
                return false;

        return true;
    }

    private void downloadBooks(DatabaseReference ref) {
        try {
            ref.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        for (DataSnapshot data : dataSnapshot.getChildren()) {
                            booksRefs.add(data.getValue().toString());
                        }
                        getBooksFromDB();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    System.out.println("The read failed: ");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void changeStatus() {
        myDataset.clear();
        mAdapter.notifyDataSetChanged();
        addList();
    }

    @Override
    public void onBookClick(int position) {
        BookObject b = myDataset.get(position);

        Intent intent = new Intent(BookLibrary.this, PlayBook.class);
        intent.putExtra("book", b);
        startActivity(intent);
    }
}