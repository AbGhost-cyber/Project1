package com.example.taskmanagerpro;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.text.DateFormat;
import java.util.Calendar;


public class CreateTaskActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_ID = "com.example.todomadeasy.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.todomadeasy.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.example.todomadeasy.EXTRA_DES";
    public static final String EXTRA_DATE = "com.example.todomadeasy.EXTRA_DATE";
    private EditText titleTask;
    private EditText Description;
    private Calendar c;
    private TextView TaskTime;

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

        saveTask.setOnClickListener (v -> {
            CreateTaskActivity.this.SaveCreatedTask ();

        });

        canceltask.setOnClickListener (v -> {
            Intent a = new Intent (CreateTaskActivity.this, MainActivity.class);
            CreateTaskActivity.this.startActivity (a);
        });
    }

    private void SaveCreatedTask() {
        String title = titleTask.getText ().toString ();
        String description = Description.getText ().toString ();
        String time = DateFormat.getTimeInstance (DateFormat.SHORT).format (c.getTime ());

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
        TaskTime.setText (time);
        finish ();

    }

    @Override
    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
        c.set (Calendar.HOUR_OF_DAY, hourOfDay);
        c.set (Calendar.MINUTE, minute);
        c.set (Calendar.SECOND, 0);

        //start alarm on timeset
        fireNotification (c);
    }

    private void fireNotification(Calendar c) {
        Intent intent = new Intent (this, AlertReceiver.class);
        String title = titleTask.getText ().toString ();
        String des = Description.getText ().toString ();
        String time = DateFormat.getTimeInstance (DateFormat.SHORT).format (c.getTime ());


        intent.putExtra (EXTRA_TITLE, title);
        intent.putExtra (EXTRA_DESC, des);
        intent.putExtra (EXTRA_DATE, time);

        AlarmManager alarmManager = (AlarmManager) getSystemService (ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast (this,
                AlertReceiver.getID (),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        if (c.before (Calendar.getInstance ())) {
            c.add (Calendar.DATE, 1);
        }
        if (alarmManager != null) {
            alarmManager.setExact (AlarmManager.RTC_WAKEUP, c.getTimeInMillis (), pendingIntent);
        }

    }


}
