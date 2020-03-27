package com.example.taskmanagerpro.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.data.USER;
import com.example.taskmanagerpro.fragments.HomeFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class SignupActivity extends AppCompatActivity {

    EditText mname,mpassword,username;
    Button signup;
    TextView mLoginButton;
    FirebaseAuth fAuth;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mname=findViewById(R.id.username);
        mpassword=findViewById(R.id.pass);
        mLoginButton=findViewById(R.id.Login);
        fAuth=FirebaseAuth.getInstance();
        progressBar=findViewById(R.id.progressBar);
        signup=findViewById(R.id.signupbutton);
        username=findViewById(R.id.Username);

        if(fAuth.getCurrentUser() !=null)
        {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }


        signup.setOnClickListener(v -> {
            final String email, password,userName,image="";
            email = mname.getText().toString().trim();
            password = mpassword.getText().toString().trim();
            userName = username.getText().toString().trim();


            if (TextUtils.isEmpty(email)) {
                mname.setError("please input email");
                return;
            }

            if (TextUtils.isEmpty(userName)) {
                username.setError("please input username");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mpassword.setError("please input password");
                return;
            }

            if (password.length() < 6) {
                mpassword.setError("password must be >=6 characters");
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            if(!HomeFragment.HasActiveNetworkConnection (this)){
                StyleableToast.makeText (this,
                        "no network connection",R.style.myToast1).show ();
                progressBar.setVisibility (View.INVISIBLE);
            }


            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    USER user=new USER(userName,email,image)
                    {
                    };

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(task1 -> {
                                progressBar.setVisibility(View.GONE);
                                if(task1.isSuccessful()){
                                    StyleableToast.makeText (SignupActivity.this,"User Created",R.style.myToast2).show ();
                                }
                                else{
                                    StyleableToast.makeText (SignupActivity.this,"an Error Occurred",R.style.myToast1).show ();
                                }

                            });

                    SignupActivity.this.startActivity(new Intent(SignupActivity.this.getApplicationContext(), MainActivity.class));
                    SignupActivity.this.finish();
                } else {
                    progressBar.setVisibility(View.GONE);
                    StyleableToast.makeText (SignupActivity.this,task.getException().getMessage(),R.style.myToast1).show ();
                }
            });

        });
        mLoginButton.setOnClickListener (v -> {
            Intent intent=new Intent (getApplicationContext (), LoginActivity.class);
            startActivity (intent);
            finish ();
        });

    }
}

