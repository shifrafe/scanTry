//create new book for user
package com.example.myapplication.create_book;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.BaseMenu;
import com.example.myapplication.BookObject;
import com.example.myapplication.HomePage;
import com.example.myapplication.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.myapplication.GlobalHelpFunction.getBitmapFromVectorDrawable;
import static com.example.myapplication.GlobalHelpFunction.getDefultImage;

public class CreateBookActivity extends BaseMenu implements PagesAdapter.OnPageListener {

    private Button save;
    private ImageView addPage;
    private EditText name;
    private EditText author;
    private CheckBox savePublic;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private PagesAdapter mAdapter;
    private List<PageModel> myDataset;
    private String userId;
    private DatabaseReference database;
    private DatabaseReference userRef;
    private StorageReference store;
    private int pagesCreate;
    private ProgressDialog loading;
    private int po;//current page that had been clickd

    private static final int REQEST_PREMISSION_CODE = 1000;
    private static final int REQUEST_CODE = 99;
    private static final int CAMERA_REQUEST_CODE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_book);

        if (!checkPermissionFromDevice())
            requestForSpecificPermission();

        loading = new ProgressDialog(this);
        loading.setTitle("Saving...");
        loading.setCancelable(false);
        addPage = findViewById(R.id.addPage);
        recyclerView = findViewById(R.id.recyclerV);
        save = findViewById(R.id.saveBook);
        name = findViewById(R.id.nameBook);
        author = findViewById(R.id.authorBook);
        savePublic = findViewById(R.id.publicBook);
        store = FirebaseStorage.getInstance().getReference();
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
        database = FirebaseDatabase.getInstance().getReference("books");
        save.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (IsCompleteBook())
                    saveBook();
            }

        });


        addPage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addList();
                mAdapter.notifyDataSetChanged();
            }

        });

        pagesCreate = 0;
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        myDataset = new ArrayList<>();
        addList();

        mAdapter = new PagesAdapter(this, myDataset, this);
        recyclerView.setAdapter(mAdapter);


    }

    //add page
    public void addList() {
        addPage.setImageBitmap(getBitmapFromVectorDrawable(this, R.drawable.ic_add));
        Bitmap icon = getBitmapFromVectorDrawable(this, R.drawable.ic_camera);
        myDataset.add(new PageModel(icon, pagesCreate));
        pagesCreate++;
    }


    public void takePhoto() {
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);

        // Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //if (takePictureIntent.resolveActivity(getPackageManager()) != null)
        //  startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
    }

    //check if user insert all the detailsof the book
    public Boolean IsCompleteBook() {
        boolean res = true;
        if (name.getText().toString().trim().matches("")) {
            name.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
            res = false;
        }
        if (author.getText().toString().trim().matches("")) {
            author.setHintTextColor(getResources().getColor(android.R.color.holo_red_light));
            res = false;
        }

        if (myDataset.size() <= 0) {
            addPage.setImageBitmap(getBitmapFromVectorDrawable(this, R.drawable.ic_add_error));
            res = false;
        }
        if (!res)
            Toast.makeText(this, "complete requests", Toast.LENGTH_SHORT).show();

        return res;
    }

    //upload book into the firebase
    public void saveBook() {
        loading.show();

        database = FirebaseDatabase.getInstance().getReference("books");
        String mname = name.getText().toString();
        String mauthor = author.getText().toString();
        Boolean isPublic = savePublic.isChecked();
        BookObject book = new BookObject(mname, mauthor, isPublic);
        DatabaseReference temp;
        temp = database.push();

        //save pages:
        for (int i = 0; i < myDataset.size(); i++) {
            String imageUrl = uploadeImage(i, temp.getKey());
            String voiceUrl = uploadeVoice(i, temp.getKey());

            if (imageUrl != null)
                book.setHaveImages(true);
            else if (i == 0)
                imageUrl = getDefultImage();
            if (voiceUrl != null)
                book.setHaveRecoreds(true);
            book.addPage(imageUrl, voiceUrl);
        }
        //save book
        book.setId(temp.getKey());
        temp.setValue(book);
        Log.i("save", "book");

        //save public id
        if (book.isSavePablic()) {
            database = FirebaseDatabase.getInstance().getReference("public_books");
            database.child(book.getId()).setValue(book.getId());
        }

        //insert the id to user
        userRef.child("my_books").child(book.getId()).setValue(book.getId());

        finishLoading();
        Intent intent = new Intent(CreateBookActivity.this, HomePage.class);
        startActivity(intent);

    }

    private void finishLoading() {
        new CountDownTimer(9000, 1000) {
            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                loading.dismiss();
            }
        }.start();
    }

    public String uploadeVoice(int index, String id) {

        if (!myDataset.get(index).getHaveRecord())
            return null;

        Uri file = Uri.fromFile(new File(myDataset.get(index).getOutputFile()));
        StorageReference audioRef = store.child(userId).child(id).child("page " + String.valueOf(index)).child(file.getLastPathSegment());

        UploadTask uploadTask = audioRef.putFile(file);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(CreateBookActivity.this, "save audio faild", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateBookActivity.this, "save audio sucssed", Toast.LENGTH_LONG).show();
            }
        });

        return audioRef.getPath();
    }


    public String uploadeImage(int index, String id) {

        if (!myDataset.get(index).getHaveImage())
            return null;

        StorageReference imageRef = store.child(userId).child(id).child("page " + String.valueOf(index)).child("image.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        myDataset.get(index).getBit().compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] d = baos.toByteArray();

        UploadTask uploadTask = imageRef.putBytes(d);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(CreateBookActivity.this, "conectiin faild", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(CreateBookActivity.this, "conectiin success", Toast.LENGTH_LONG).show();
            }
        });


        return imageRef.getPath();//urlTask.toString();
    }

    //  @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                myDataset.get(po).setBit(bitmap);
                mAdapter.notifyDataSetChanged();

            } catch (IOException e) {
                e.printStackTrace();
            }
       /*
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
            myDataset.get(po).setBit(imageBitmap);
            mAdapter.notifyDataSetChanged();
        }
        */

        }
    }

    private boolean checkPermissionFromDevice() {
        int write_external = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        int account = ContextCompat.checkSelfPermission(this, Manifest.permission.GET_ACCOUNTS);

        int camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        return (write_external == PackageManager.PERMISSION_GRANTED && record == PackageManager.PERMISSION_GRANTED
                && camera == PackageManager.PERMISSION_GRANTED && account == PackageManager.PERMISSION_GRANTED);

    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA,
                Manifest.permission.GET_ACCOUNTS,
                Manifest.permission.RECORD_AUDIO}, REQEST_PREMISSION_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQEST_PREMISSION_CODE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onCameraClick(int position) {
        po = position;
        takePhoto();
    }

    @Override
    public void onDeleteClick(int position) {
        myDataset.remove(myDataset.get(position));
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public void onPlayClick(int position) {
        myDataset.get(position).playRecored();
        Toast.makeText(this, "Play Recording...", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onPuseClick(int position) {
        myDataset.get(position).stopRecored();
        Toast.makeText(this, "Stop Recording...", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onRecordClick(int position) {
        myDataset.get(position).startRecored();
        Toast.makeText(this, "Recording...", Toast.LENGTH_LONG).show();
    }

}