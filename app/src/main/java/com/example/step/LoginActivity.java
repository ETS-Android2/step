package com.example.step;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {
    public static final String SHARED_PREFS = "sharedPrefs";
    public String userUID;
    EditText email,password;
    Button signInButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;
    TextView createAccount;

    public LocationManager manager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        loadUserUID();
        if(!userUID.equals("")){
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_login);
        fAuth = FirebaseAuth.getInstance();
        email= (EditText)findViewById(R.id.email);
        password = (EditText)findViewById(R.id.password);
        signInButton = (Button)findViewById(R.id.login);
        progressBar = (ProgressBar)findViewById(R.id.loading);
        createAccount = (TextView)findViewById(R.id.link);
        //Toast.makeText(LoginActivity.this,"Lets do login",Toast.LENGTH_LONG).show();


        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),RegisterActivity.class));
                finish();
                return;

            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailStr = email.getText().toString().trim();
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
                fAuth.signInWithEmailAndPassword(emailStr,passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(LoginActivity.this,"Successfully login ",Toast.LENGTH_LONG).show();
                            saveUserUID(fAuth.getCurrentUser().getUid());
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                            finish();
                            return;
                        }
                        else{
                            Toast.makeText(LoginActivity.this,"Error ! "+task.getException().getMessage(),Toast.LENGTH_LONG).show();
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
    public void loadUserUID(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        userUID = sharedPreferences.getString("userUID","");
    }


}
