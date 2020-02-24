package com.example.taskmanagerpro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.firebase.auth.FirebaseAuth;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class LogoutDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
        builder.setTitle ("Task Manager Pro");
        builder.setMessage("Do you wish to Log out?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    FirebaseAuth.getInstance ().signOut ();
                    Intent Myintent = new Intent (LogoutDialogFragment.this.getContext (), SignupActivity.class);
                    StyleableToast.makeText (getContext (),"Successfully logged out",R.style.ToastExitApp).show ();
                    LogoutDialogFragment.this.startActivity (Myintent);
                    LogoutDialogFragment.this.getActivity ().finish ();
                    dialog.dismiss ();

                })
                .setNegativeButton("No", (dialog, which) -> {
                    dialog.dismiss ();
                    StyleableToast.makeText (getContext (),"Cancelled",R.style.myToast1).show ();
                }
                );

        return builder.create();

    }
}
