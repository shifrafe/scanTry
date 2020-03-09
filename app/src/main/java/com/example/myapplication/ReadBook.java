package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myapplication.create_book.PageObject;
import com.example.myapplication.widget.PhotoFullPopupWindow;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.GlobalHelpFunction.getDefultImageBitmap;

public class ReadBook extends BaseMenu {

    private BookObject book;
    private ImageView image;
    private SeekBar time;
    private ImageButton backPage;
    private ImageButton nextPage;
    private ImageButton restart;
    private TextView numPages;
    private TextView curPageShow;
    private ArrayList<Bitmap> m_images;
    private int curPage;
    private boolean isImageFitToScreen;
    private int imageHight;
    public static float screen_width;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_book);

        init();


        m_images = new ArrayList<>();
        start(book.getPageObjects());

    }

    private void init() {

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screen_width = metrics.widthPixels;

        this.book = (BookObject) getIntent().getSerializableExtra("book");
        this.image = findViewById(R.id.imageRead);
        this.time = findViewById(R.id.timeRead);
        this.backPage = findViewById(R.id.backPageRead);
        this.nextPage = findViewById(R.id.nextPageRead);
        this.restart = findViewById(R.id.restartRead);
        this.numPages = findViewById(R.id.numPagesRead);
        this.curPageShow = findViewById(R.id.curPageRead);

        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorRead)));

        numPages.setText(Integer.toString(book.getPageObjects().size()));
        FrameLayout frame = findViewById(R.id.frameImage);
        imageHight = frame.getLayoutParams().height;
        start();
        addlisean();
    }

    private void addlisean() {
        nextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passPage();
            }
        });

        backPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PrevPage();
            }
        });

        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartBook();

            }
        });

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable)image.getDrawable()).getBitmap();
                new PhotoFullPopupWindow(ReadBook.this,v,null,bitmap);
//                image.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        FrameLayout frame = findViewById(R.id.frameImage);
//
//                        if(isImageFitToScreen) {
//                            Log.i("if",Integer.toString(frame.getLayoutParams().height));
//
//                            isImageFitToScreen=false;
//                            frame.getLayoutParams().height = imageHight; // LayoutParams: android.view.ViewGroup.LayoutParams
//                            frame.requestLayout();//It is necesary to refresh the screen
//
//                            Log.i("if",Integer.toString(frame.getLayoutParams().height));
//
//
//                        }else{
//                            isImageFitToScreen=true;
//                            Log.i("else",Integer.toString(frame.getLayoutParams().height));
//
//                            frame.getLayoutParams().height = FrameLayout.LayoutParams.MATCH_PARENT; // LayoutParams: android.view.ViewGroup.LayoutParams
//                            frame.requestLayout();//It is necesary to refresh the screen
//                            Log.i("else",Integer.toString(frame.getLayoutParams().height));
//                         //   image.setAdjustViewBounds(true);
//
//
//                        }
//                    }
//                });



            }
        });
    }

    private void start() {
        image.setImageBitmap(getDefultImageBitmap(this));
        curPage = 0;
        nextPage.setImageAlpha(225);
        nextPage.setEnabled(true);
        backPage.setImageAlpha(100);
        backPage.setEnabled(false);
        isImageFitToScreen = false;

        if (book.getPageObjects().size() == 1) {
            nextPage.setImageAlpha(100);
            nextPage.setEnabled(false);
        }
        updateProgress();

    }

    private void restartBook() {
        start();
        image.setImageBitmap(m_images.get(curPage));
        //image.setScaleType(image.getpar);
    }

    private void updateProgress() {
        int size = book.getPageObjects().size();
        int part = 100 / size;
        time.setProgress(part * (curPage + 1));

        curPageShow.setText("page number " + Integer.toString(curPage + 1));

    }

    private void PrevPage() {
        curPage -= 1;
        image.setImageBitmap(m_images.get(curPage));
        if (curPage == 0) {
            backPage.setImageAlpha(100);
            backPage.setEnabled(false);
        }

        nextPage.setImageAlpha(255);
        nextPage.setEnabled(true);

        updateProgress();
    }

    private void passPage() {

        curPage += 1;
        if (m_images.size() <= curPage) {
            curPage -= 1;
            return;

        }
        image.setImageBitmap(m_images.get(curPage));
        if (curPage == book.getPageObjects().size() - 1) {
            nextPage.setImageAlpha(100);
            nextPage.setEnabled(false);
        }

        backPage.setImageAlpha(255);
        backPage.setEnabled(true);
        updateProgress();
    }

    private void start(List<PageObject> pages) {
        downloadPage(0);

    }

    private void downloadPage(final int i) {
        StorageReference store;
        try {
            store = FirebaseStorage.getInstance().getReferenceFromUrl("gs://booklibrary-f24cd.appspot.com" + book.getPageObjects().get(i).getImage());

        } catch (Exception e) {
            m_images.add(i, getDefultImageBitmap(image.getContext()));

            if (i == 0)
                image.setImageBitmap(m_images.get(0));

            int next = i + 1;
            if (next >= book.getPageObjects().size())
                return;
            downloadPage(next);
            return;
        }
        store.getBytes(1024 * 1024).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {

                m_images.add(i, BitmapFactory.decodeByteArray(bytes, 0, bytes.length));

                if (i == 0)
                    image.setImageBitmap(m_images.get(0));

                int next = i + 1;
                if (next >= book.getPageObjects().size())
                    return;

                downloadPage(next);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });


    }
}

