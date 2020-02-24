package com.example.taskmanagerpro;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
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
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

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


public class ProfileFragment extends Fragment {
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;
    private ImageView avatar;
    private Button upload;
    private Uri imgUrl;
    public static final int CHOOSE_IMAGE = 1;
    private DatabaseReference databaseReference;
    private StorageReference mStorageRef;
    private StorageTask mUpload;
    private TextView aboutAppTv, Name, Email, Share,sendFeedback;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate (R.layout.profile_layout, container, false);
        getActivity ().setTitle ("My Profile");

        firebaseAuth = FirebaseAuth.getInstance ();
        user = firebaseAuth.getCurrentUser ();
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance ();
        databaseReference = firebaseDatabase.getReference ("Users");

        avatar = v.findViewById (R.id.avatarIv);
        Name = v.findViewById (R.id.User_name);
        Email = v.findViewById (R.id.User_email);
        Share = v.findViewById (R.id.shareAndSend);
        upload = v.findViewById (R.id.saveUpload);
        sendFeedback=v.findViewById (R.id.sendFeedback);
        TextView logoutTV = v.findViewById (R.id.logOutApp);
        aboutAppTv = v.findViewById (R.id.about);
        mStorageRef = FirebaseStorage.getInstance ().getReference ("uploads");


        upload.setOnClickListener (v12 -> {
            if (mUpload != null && mUpload.isInProgress ()) {
                StyleableToast.makeText (getContext (), "upload is in progress", R.style.myToast1);
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
                    String email = "" + ds.child ("email").getValue ();
                    String urlImage = "" + ds.child ("image").getValue ();
                    Name.setText (name);
                    Email.setText (String.format ("email: %s", email));
                    try {
                        Picasso.with (getContext ())
                                .load (urlImage).fit ().into (avatar);
                    } catch (Exception e) {
                        e.printStackTrace ();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //log out function
        logoutTV.setOnClickListener (v1 -> {
            DialogFragment dialogFragment = new LogoutDialogFragment ();
            dialogFragment.setCancelable (false);
            dialogFragment.show (getActivity ().getSupportFragmentManager (), "log out");
        });

        //share function
        Share.setOnClickListener (v14 -> {
            Intent a = new Intent (Intent.ACTION_SEND);
            final String appPackageName = getActivity ().getApplicationContext ().getPackageName ();
            String strAppLink;
            try {
                strAppLink = "https://play.google.com/store/apps/details?id" + appPackageName;
            } catch (android.content.ActivityNotFoundException anfe) {
                strAppLink = "https://play.google.com/store/apps/details?id" + appPackageName;
            }
            a.setType ("text/link");
            String sharebOdy = "Hey, Check out Task Manager Pro, i use it to manage my Todo's. Get it for free at " + "\n" + "" + strAppLink;
            String shareSub = "APP NAME/TITLE";
            a.putExtra (Intent.EXTRA_SUBJECT, shareSub);
            a.putExtra (Intent.EXTRA_TEXT, sharebOdy);
            startActivity (Intent.createChooser (a, "SHare Using"));
        });

        //send Feedback Function
        sendFeedback.setOnClickListener (v15 -> showFeedbackDialog (getContext ()));


        return v;
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
        ContentResolver resolver = getActivity ().getContentResolver ();
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
