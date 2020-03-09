package com.example.myapplication.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.ProgressBar;

import androidx.palette.graphics.Palette;

import com.example.myapplication.R;
import com.example.myapplication.Utils;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;


import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PhotoFullPopupWindow extends PopupWindow {
 
    View view;
    Context mContext;
    PhotoView photoView;
    ProgressBar loading;
    ViewGroup parent;
    private static PhotoFullPopupWindow instance = null;
 
 
 
    public PhotoFullPopupWindow(Context ctx,  View v, String imageUrl, Bitmap bitmap) {
        super(((LayoutInflater) ctx.getSystemService(LAYOUT_INFLATER_SERVICE)).inflate( R.layout.image_view_gallery, null), ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
 
        if (Build.VERSION.SDK_INT >= 21) {
            setElevation(5.0f);
        }
        this.mContext = ctx;
        this.view = getContentView();
        ImageButton closeButton = (ImageButton) this.view.findViewById(R.id.ib_close);
        setOutsideTouchable(true);
        setFocusable(true);
        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                dismiss();
            }
        });
        //---------Begin customising this popup--------------------
 
        photoView = (PhotoView) view.findViewById(R.id.image);
        loading = (ProgressBar) view.findViewById(R.id.loading);
        photoView.setMaximumScale(6);
        parent = (ViewGroup) photoView.getParent();
        // ImageUtils.setZoomable(imageView);
        //----------------------------
        if (bitmap != null) {
            loading.setVisibility(View.GONE);
            Matrix matrix = new Matrix();
            //here you rotate the bitmap, could be useful to implement a rotating button or preload rotated image
            matrix.postRotate(90);
//            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 50, 50, false);
            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);

            if (Build.VERSION.SDK_INT >= 16) {
                parent.setBackground(new BitmapDrawable(mContext.getResources(), Utils.fastblur(rotatedBitmap)));// ));

            } else {
                onPalette(Palette.from(bitmap).generate());
 
            }
            photoView.setImageBitmap(rotatedBitmap);
        } else {
            loading.setIndeterminate(true);
            loading.setVisibility(View.VISIBLE);
            Picasso.get().load(imageUrl).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap resource, Picasso.LoadedFrom from) {

                        parent.setBackground(new BitmapDrawable(mContext.getResources(), Utils.fastblur(Bitmap.createScaledBitmap(resource, 50, 50, true))));// ));

                    photoView.setImageBitmap(resource);

                    loading.setVisibility(View.GONE);


                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    loading.setIndeterminate(false);
                    loading.setBackgroundColor(Color.LTGRAY);
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });

 

        }
        showAtLocation(v, Gravity.CENTER, 0, 0);
        //------------------------------
 
    }
 
    public void onPalette(Palette palette) {
        if (null != palette) {
            ViewGroup parent = (ViewGroup) photoView.getParent().getParent();
            parent.setBackgroundColor(palette.getDarkVibrantColor(Color.GRAY));
        }
    }
 
}