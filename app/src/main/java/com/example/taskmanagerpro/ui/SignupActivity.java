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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        mname=findViewById(R.id.username);
        mpassword=findViewById(R.id.pass);
        mLoginButton=findViewById(R.id.Login);
        fAuth=FirebaseAuth.getInstance();
        signup=findViewById(R.id.signupbutton);
        username=findViewById(R.id.Username);
        LoadingProgressDialog progressDialog=new LoadingProgressDialog (this);

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
            //checks for internet connection
            if(HomeFragment.HasActiveNetworkConnection (this)){
              progressDialog.startAlertDialog ();
            }
            else{
                StyleableToast.makeText (this,
                        "no network connection",R.style.myToast1).show ();
               //progressDialog.dismissDialog ();
                return;
            }

            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                progressDialog.dismissDialog ();
                if (task.isSuccessful()) {
                    USER user=new USER(userName,email,image)
                    {
                    };

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(task1 -> {
                                if(task1.isSuccessful()){
                                    StyleableToast.makeText (SignupActivity.this,"User Created",R.style.myToast2).show ();
                                }
                            });

                    SignupActivity.this.startActivity(new Intent(SignupActivity.this.getApplicationContext(), MainActivity.class));
                    SignupActivity.this.finish();
                } else {
                    progressDialog.dismissDialog ();
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

