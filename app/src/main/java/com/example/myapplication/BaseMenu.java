package com.example.myapplication;

import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.login.MainActivity;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BaseMenu extends AppCompatActivity {
    public DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
    public String currUserId;

    public void sign_out() {
        //HomePage out
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText( getApplicationContext(),"You have signed out", Toast.LENGTH_LONG).show();

                        Intent intent = new Intent(BaseMenu.this, MainActivity.class);
                        startActivity(intent);
                    }
                });

    }

    public void delete_account() {
        currUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        //delete account
        AuthUI.getInstance()
                .delete(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //notice that currUser is initiate at the begin

                        Log.i("delete user- id", currUserId);
                        ref.child(currUserId).setValue(null);

                        Toast.makeText(getApplicationContext(), "Your account deleted successfully", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(BaseMenu.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                sign_out();
                return true;
            case R.id.delete_account_menu:
                delete_account();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }



}
