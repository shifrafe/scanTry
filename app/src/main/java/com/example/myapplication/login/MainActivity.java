package com.example.myapplication.login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


import com.example.myapplication.HomePage;
import com.example.myapplication.R;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.BuildConfig;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    FirebaseDatabase database;
    DatabaseReference ref;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = FirebaseDatabase.getInstance();
        ref = database.getReference("Users");
        mAuth = FirebaseAuth.getInstance();
        // Check if user is signed in (non-null) and go to the next activity.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.i("info", "user in");
            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
        }
        else {
            Log.i("info", "user out");
            sign_in();
        }

    }

    //HomePage in and create a new account
    public void sign_in() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build()/*,
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.TwitterBuilder().build()*/);

        // Create and launch HomePage-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(!BuildConfig.DEBUG /* credentials */, true /* hints */)
                .setLogo(R.drawable.logo)
                        .setTheme(R.style.AppTheme)
                        .build(),
                RC_SIGN_IN);
    }

   /* @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and go to the next activity.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.i("info", "user in");

            Intent intent = new Intent(MainActivity.this, HomePage.class);
            startActivity(intent);
            //textView.setText(ref.child(currentUser.getUid()).getKey());
        }
        else {
            //textView.setText("HomePage out");
            Log.i("info", "user out");
        }
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try {
            if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);//////

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Boolean exist = false;      //if the user already exist in the database
                        FirebaseUser user = mAuth.getCurrentUser();
                        for(DataSnapshot data: dataSnapshot.getChildren())
                            if (data.getKey().equals(user.getUid())) {
                               exist = true;
                                break;
                            }

                        //create user in the database only if this user don't have already data
                        if (!exist) {
                            try{
                                ref.child(user.getUid()).setValue(user);

                            }
                            catch (Throwable e) {
                                //OutOfMemoryError
                                Log.i("exception", e.toString());
                                Log.i("exception", "need to delete account");
                                Toast.makeText(getApplicationContext(), "error! try again later", Toast.LENGTH_SHORT).show();
                                //delete the account, because can't create and insert its data in the firebase
                                mAuth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Log.i("exception", "exception handling");
                                    }
                                });
                                throw e;
                            }
                        }

                        Intent intent = new Intent(MainActivity.this, HomePage.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        //avital: i don;t know when it's accure
                        Log.i("ERROR","error in onCancelled func in main activity");
                        Log.i("ERROR",databaseError.getDetails());
                    }
                });

            }
            else {
                // Sign in failed. If response is null the user canceled the
                // HomePage-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Toast.makeText(getApplicationContext(),
                        "error to connect to the account or HomePage in, try later",
                        Toast.LENGTH_LONG).show();
                Log.i("ERRORR", response.getError().toString());

            }
        }
    }catch (Throwable throwable){

        }
    }
}
