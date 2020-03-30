package com.example.taskmanagerpro.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.taskmanagerpro.R;

import java.util.Objects;

public class LoadingProgressDialog {

   private Activity activity;
   private AlertDialog dialog;

    public LoadingProgressDialog(Activity activity) {
        this.activity = activity;
    }

    public void startAlertDialog(){
        AlertDialog.Builder builder=new AlertDialog.Builder (activity);

        LayoutInflater inflater=activity.getLayoutInflater ();
        View layoutView=inflater.inflate (R.layout.customdialog, null);
        builder.setView (layoutView);
        builder.setCancelable (false);
        dialog=builder.create ();

        Objects.requireNonNull (dialog.getWindow ()).clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        dialog.getWindow ().setBackgroundDrawable (new ColorDrawable (Color.TRANSPARENT));
        dialog.show ();
    }

    public void dismissDialog(){
        if(dialog.isShowing ()){
            dialog.dismiss ();
        }
    }
}
