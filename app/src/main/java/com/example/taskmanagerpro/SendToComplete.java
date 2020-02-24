package com.example.taskmanagerpro;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class SendToComplete extends AppCompatActivity {
  Button CompleteTask;
    String title,des,time;
    public static final int RESULTCODE=2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_send_to_complete);
        TextView setDesc = findViewById (R.id.setDescription);
        TextView setDate = findViewById (R.id.setTime);
        TextView setTitle = findViewById (R.id.setTitle);
        CompleteTask=findViewById (R.id.CompleteTask);

        setTitle ("Task Alarm");

        Intent intent=getIntent ();
        title=intent.getStringExtra ("Title");
        des=intent.getStringExtra ("Description");
        time=intent.getStringExtra ("Time");

        setTitle.setText (title);
        setDesc.setText (des);
        setDate.setText (time);


        CompleteTask.setOnClickListener (v -> {
            SendToComplete.this.showCompleteTaskDialog ();
            CancelNotification ();
        });

    }

    private void showCompleteTaskDialog() {
        AlertDialog.Builder builder=new AlertDialog.Builder (this);
        builder.setTitle ("Complete Task?");
        builder.setMessage ("do you wish to complete this task?");
        builder.setCancelable (false);

        builder.setPositiveButton ("Yes", (dialog, which) -> SendToComplete.this.sendToComplete ());
        builder.setNegativeButton ("No", (dialog, which) -> {
            dialog.dismiss ();
            finish ();
            StyleableToast.makeText (getApplicationContext (),"Cancelled",R.style.myToast1).show ();

        });
        builder.create ().show ();
    }
    //create method that sends data to mainActivity, then CompletedTaskfragment takes it from there
    private void sendToComplete() {
        Intent data = new Intent (this,MainActivity.class);
        data.putExtra ("Title", title);
       data.putExtra ("Time", time);
        startActivity (data);
        finish ();
        StyleableToast.makeText (this,"Task Completed",R.style.myToast).show ();
    }
    //method that cancels and update the notification
    private void CancelNotification(){
        Intent intent = new Intent (getApplicationContext (), AlertReciever.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext (), RESULTCODE, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getApplicationContext ().getSystemService (Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }
}
