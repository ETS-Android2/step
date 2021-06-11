package com.example.step;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    public static final String SHARED_PREFS = "sharedPrefs";

    EditText email,password;
    Button RegisterButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    TextView LogIn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        fAuth = FirebaseAuth.getInstance();
        email= (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        RegisterButton = (Button)findViewById(R.id.register);
        progressBar = (ProgressBar)findViewById(R.id.loading);
        LogIn = (TextView)findViewById(R.id.link);


        LogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
                return;
            }
        });

        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String emailStr = email.getText().toString().trim();
                String passwordStr = password.getText().toString().trim();
                if(TextUtils.isEmpty(emailStr)){
                    email.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(passwordStr)){
                    password.setError("Password is Required");
                    return;
                }
                if(password.length()<6){
                    password.setError("Password must be >=6 characters");
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                //authenticate the user

                fAuth.createUserWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseDatabaseHelper fdb = new FirebaseDatabaseHelper();
                            fdb.addUser(fAuth.getUid(),emailStr);
                            saveUserUID(fdb.getKey());
                            Toast.makeText(RegisterActivity.this,"Successfully register",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                            return;
                        }
                        else{
                            Toast.makeText(RegisterActivity.this,"Error ! "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


            }
        });
    }

    public void saveUserUID(String id){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("userUID",id);
        editor.apply();
    }
}
