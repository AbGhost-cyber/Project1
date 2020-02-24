package com.example.taskmanagerpro;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;


public class CreateTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_ID = "com.example.todomadeasy.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.todomadeasy.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.example.todomadeasy.EXTRA_DES";
    public static final String EXTRA_DATE = "com.example.todomadeasy.EXTRA_DATE";
    public static final int RESULTCODE = 1;
    private EditText titleTask;
    private EditText Description;
    static Calendar c;
    static String time;
    static String title;
    static String description;
    private TextView TaskTime;
    public static AlarmManager alarmManager;
   public  Intent intent;
   public static PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_create_task);

        titleTask = findViewById (R.id.title_Task);
        Description = findViewById (R.id.task_Des);
        TaskTime = findViewById (R.id.task_time);
        Button saveTask = findViewById (R.id.SaveTask);
        Button canceltask = findViewById (R.id.Cancel_Task);
        Button timePicker1 = findViewById (R.id.TimePicker);
        c = Calendar.getInstance ();

        timePicker1.setOnClickListener (v -> {
            DialogFragment timePicker = new TimePickerFragment ();
            timePicker.show (CreateTaskActivity.this.getSupportFragmentManager (), "time picker");
        });


        Intent intent = getIntent ();

        if (intent.hasExtra (EXTRA_ID)) {

            titleTask.setText (intent.getStringExtra (EXTRA_TITLE));
            Description.setText (intent.getStringExtra (EXTRA_DESC));
            TaskTime.setText (intent.getStringExtra (EXTRA_DATE));
            saveTask.setText (R.string.Task);
            canceltask.setText (R.string.CancelUpdate);
            setTitle ("Edit Task");


        } else {
            setTitle ("Create Task");
        }

        saveTask.setOnClickListener (v -> CreateTaskActivity.this.SaveCreatedTask ());

        canceltask.setOnClickListener (v -> {
            Intent a = new Intent (CreateTaskActivity.this, MainActivity.class);
            CreateTaskActivity.this.startActivity (a);
        });
    }

    private void SaveCreatedTask() {
        title = titleTask.getText ().toString ();
        description = Description.getText ().toString ();
        time = DateFormat.getTimeInstance (DateFormat.SHORT).format (c.getTime ());

        if (TextUtils.isEmpty (title)) {
            titleTask.setError ("please input a Task");
            return;
        }

        if (TextUtils.isEmpty (description)) {
            Description.setError ("Describe it");
            return;
        }
        Intent data = new Intent ();
        data.putExtra (EXTRA_TITLE, title);
        data.putExtra (EXTRA_DESC, description);
        data.putExtra (EXTRA_DATE, time);


        int id = getIntent ().getIntExtra (EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra (EXTRA_ID, id);
        }
        setResult (RESULT_OK, data);
        finish ();
        TaskTime.setText (time);
    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        c.set (Calendar.HOUR_OF_DAY, hourOfDay);
        c.set (Calendar.MINUTE, minute);
        c.set (Calendar.SECOND, 0);

        fireNotification (c);
    }

    private void fireNotification(Calendar c) {
         alarmManager = (AlarmManager) getSystemService (Context.ALARM_SERVICE);
         intent = new Intent (getApplicationContext (), AlertReciever.class);

         pendingIntent = PendingIntent.getBroadcast (this, RESULTCODE, intent, 0);
        if (c.before (Calendar.getInstance ())) {
            c.add (Calendar.DATE, 1);
        }


        assert alarmManager != null;
        alarmManager.setExact (AlarmManager.RTC_WAKEUP, c.getTimeInMillis (), pendingIntent);
    }


    // task notification class
    public static class taskNotificationHelper extends ContextWrapper {
        public static final String TaskChannel1_ID = "taskChannel1ID";
        public static final String TaskChannel1_Name = "Task Alarm";
        public static final String EXTRA_TITLE = "com.example.todomadeasy.EXTRA_TITLE";

        private NotificationManager mManager;


        public taskNotificationHelper(Context base) {
            super (base);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                createChannels ();
            }

        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void createChannels() {
            NotificationChannel taskCh1 = new NotificationChannel (
                    TaskChannel1_ID,
                    TaskChannel1_Name,
                    NotificationManager.IMPORTANCE_HIGH
            );
            getManager ().createNotificationChannel (taskCh1);

        }

        public NotificationManager getManager() {
            if (mManager == null) {
                mManager = (NotificationManager) getSystemService (Context.NOTIFICATION_SERVICE);
            }
            return mManager;
        }

        public NotificationCompat.Builder getChannelNotification() {
            Intent Resultintent = new Intent (getApplicationContext (), SendToComplete.class);

            Intent getIntent=new Intent (getApplicationContext (),MainActivity.class);
            getIntent.putExtra (EXTRA_TITLE,title);

            Resultintent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Resultintent.putExtra ("Title", title);
            Resultintent.putExtra ("Description", description);
            Resultintent.putExtra ("Time", time);
            PendingIntent resultPendingIntent = PendingIntent.getActivity (getApplicationContext (), RESULTCODE, Resultintent,
                    PendingIntent.FLAG_UPDATE_CURRENT);


            return new NotificationCompat.Builder (this, TaskChannel1_ID)
                    .setContentTitle ("Right on time!")
                    .setContentText ("it's time to " + title + " Let's Go!")
                    .setColor (getResources ().getColor (R.color.colorAccent))
                    .setSmallIcon (R.drawable.ic_alarm)
                    .setContentIntent (resultPendingIntent)
                    .setAutoCancel (true)
                    .setPriority (NotificationCompat.PRIORITY_HIGH);
        }

    }
    //handles  notification swipe deleted tasks
    public static void CancelNotForDeleted(){
        alarmManager.cancel(pendingIntent);
    }
    public static void setNotForDeleted(){
        if (c.before (Calendar.getInstance ())) {
            c.add (Calendar.DATE, 1);
        }
        alarmManager.setExact (AlarmManager.RTC_WAKEUP, c.getTimeInMillis (), pendingIntent);
    }



}
