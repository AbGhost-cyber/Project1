package com.example.taskmanagerpro.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.taskmanagerpro.R;
import com.example.taskmanagerpro.ui.ActivityAbout;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import java.util.Objects;


public class ProfileFragment extends Fragment {
    private FirebaseUser user;
    private ImageView avatar;
    private Button upload;
    private Uri imgUrl;
    public static final int CHOOSE_IMAGE = 1;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private StorageTask mUpload;
    private TextView Name;
    private TextView Email;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.profile_layout, container, false);
        getActivity ().setTitle ("My Profile");

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();
        databaseReference = firebaseDatabase.getReference ("Users");

        TextView rateApp = v.findViewById (R.id.RateApp);
        avatar = v.findViewById (R.id.avatarIv);
        Name = v.findViewById (R.id.User_name);
        Email = v.findViewById (R.id.User_email);
        TextView share = v.findViewById (R.id.shareAndSend);
        upload = v.findViewById (R.id.saveUpload);
        TextView sendFeedback = v.findViewById (R.id.sendFeedback);
        TextView aboutAppTv = v.findViewById (R.id.about);
        mStorageRef = FirebaseStorage.getInstance ().getReference ("uploads");

        retrieveinfos ();

        upload.setOnClickListener (v12 -> {
            if (mUpload != null && mUpload.isInProgress ()) {
                StyleableToast.makeText (Objects.requireNonNull (getContext ()), "upload is in progress", R.style.myToast1);
            } else {
                UploadImage (this.getContext ());//method that allows user to upload selected image
                upload.setVisibility (View.GONE);
            }
        });

        aboutAppTv.setOnClickListener (v1 -> {
            Intent intent = new Intent (this.getContext (), ActivityAbout.class);//start about app activity
            startActivity (intent);

        });

        //image click
        avatar.setOnClickListener (v13 -> {
            showFileChooser ();
            upload.setVisibility (View.VISIBLE);
        });

        //get current user by email
        Query query = databaseReference.orderByChild ("email").equalTo (user.getEmail ());

        query.addValueEventListener (new ValueEventListener () {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren ()) {
                    //get data
                    String name = "" + ds.child ("username").getValue ();
                    String email = "Email:" + ds.child ("email").getValue ();
                    String urlImage = "" + ds.child ("image").getValue ();
                    Name.setText (name);
                    Email.setText (String.format ("%s", email));
                    try {
                        Picasso.with (getContext ())
                                .load (urlImage).fit ().into (avatar);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                    try {
                        SharedPreferences sharedPreferences= Objects.requireNonNull (getActivity ())
                                .getPreferences (Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor=sharedPreferences.edit ();
                        editor.putString ("Username",name);
                        editor.putString ("email",email);
                        editor.putString ("image",urlImage);
                        editor.apply ();
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //rate app function, this will come in the update
        rateApp.setOnClickListener (v16 -> {
            try{
                startActivity (new Intent (Intent.ACTION_VIEW,
                        Uri.parse ("market://details?id=" +getContext ().getPackageName ())));
            }catch (ActivityNotFoundException e){
                startActivity (new Intent (Intent.ACTION_VIEW,
                        Uri.parse ("http://play.google.com/store/apps/details?id=" +getContext ().getPackageName ())));
            }
        });

        //share function
        share.setOnClickListener (v14 -> {
            //TODO remember to replace the google play with website link
            Intent a = new Intent (Intent.ACTION_SEND);
            final String appPackageName = getActivity ().getApplicationContext ().getPackageName ();
            String strAppLink;
            try {
                strAppLink = "https://play.google.com/store/apps/details?id" + appPackageName;
            } catch (android.content.ActivityNotFoundException anfe) {
                strAppLink = "https://play.google.com/store/apps/details?id" + appPackageName;
            }
            a.setType ("text/link");
            String sharebOdy = "Hey, Check out Task-it, i use it to manage my Todo's. Get it for free at " + "\n" + "" + strAppLink;
            String shareSub = "APP NAME/TITLE";
            a.putExtra (Intent.EXTRA_SUBJECT, shareSub);
            a.putExtra (Intent.EXTRA_TEXT, sharebOdy);
            startActivity (Intent.createChooser (a, "Share Using"));
        });

        //send Feedback Function
        sendFeedback.setOnClickListener (v15 -> showFeedbackDialog (getContext ()));


        return v;
    }
    //retrieve save username & current date from sharedpreferences

    private void retrieveinfos(){
        String username= null;
        String email= null;
        String urlImage= null;
        try {
            SharedPreferences sharedPreferences= Objects.requireNonNull (getActivity ()).getPreferences (Context.MODE_PRIVATE);
            username = sharedPreferences.getString ("Username","");
            email = sharedPreferences.getString ("email","");
            urlImage = sharedPreferences.getString ("image","");
        } catch (Exception e) {
            e.printStackTrace ();
        }

        Name.setText (username);
        Email.setText (email);
        try {
            Picasso.with (getContext ())
                    .load (urlImage).fit ().into (avatar);
        } catch (Exception e) {
            e.printStackTrace ();
        }
    }

    private void showFileChooser() {
        Intent intent = new Intent ();
        intent.setType ("image/*");
        intent.setAction (Intent.ACTION_GET_CONTENT);
        startActivityForResult (intent, CHOOSE_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == CHOOSE_IMAGE && resultCode == Activity.RESULT_OK && data != null && data.getData () != null) {

            imgUrl = data.getData ();
            Picasso.with (getActivity ()).load (imgUrl).into (avatar);
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver resolver = Objects.requireNonNull (getActivity ()).getContentResolver ();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton ();
        return mimeTypeMap.getExtensionFromMimeType (resolver.getType (uri));
    }

    private void UploadImage(Context context) {
        if (imgUrl != null) {
            StorageReference file = mStorageRef.child (System.currentTimeMillis () + "*" + getFileExtension (imgUrl));

            mUpload = file.putFile (imgUrl)
                    .addOnSuccessListener (taskSnapshot -> {
                        Task<Uri> uriTasks = taskSnapshot.getStorage ().getDownloadUrl ();
                        while (!uriTasks.isSuccessful ()) ;
                        Uri downloadUri = uriTasks.getResult ();
                        if (uriTasks.isSuccessful ()) {
                            databaseReference.child (user.getUid ()).child ("image").setValue (downloadUri.toString ());
                            Picasso.with (getActivity ()).load (imgUrl).into (avatar);

                            StyleableToast.makeText (context, "Upload successfully", R.style.myToast).show ();
                            upload.setVisibility (View.INVISIBLE);
                        } else {
                            StyleableToast.makeText (context, "Some error occurred", R.style.myToast1).show ();
                            upload.setVisibility (View.INVISIBLE);
                        }
                    })
                    .addOnFailureListener (e -> Toast.makeText (getContext (), e.getMessage (), Toast.LENGTH_SHORT).show ());
        } else {
            Toast.makeText (getContext (), "no file selected", Toast.LENGTH_SHORT).show ();
        }
    }

    private void showFeedbackDialog(Context context)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Feedback Form");
        builder.setMessage("please provide us your valuable feedback");
        LayoutInflater inflater = LayoutInflater.from(getContext ());

        View reg_layout = inflater.inflate(R.layout.feedback, null);
        final TextView tvEmail = reg_layout.findViewById(R.id.emailName);
        tvEmail.setText(user.getEmail());

        final EditText Feedback = reg_layout.findViewById(R.id.Message);
        builder.setView(reg_layout);

        builder.setPositiveButton("SEND", (dialog, which) -> {
            if (TextUtils.isEmpty(Feedback.getText().toString())) {
                StyleableToast.makeText (context,"please input a text",R.style.myToast1).show ();
                return;
            }

            FirebaseDatabase database=FirebaseDatabase.getInstance();
            DatabaseReference myRef=database.getReference();

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Object value=dataSnapshot.getValue();

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(getContext (), "failed to read info", Toast.LENGTH_SHORT).show();
                }
            });
            myRef.child("Users").child(user.getUid()).child("Feedback").setValue(Feedback.getText().toString());
            StyleableToast.makeText (context,"thanks for your valuable feedback",R.style.myToast).show ();
        });
        builder.setNegativeButton("CANCEL", (dialog, which) -> dialog.dismiss());
        builder.setCancelable(false);
        builder.show();
    }
}
