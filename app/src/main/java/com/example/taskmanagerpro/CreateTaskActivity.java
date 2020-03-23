package com.example.taskmanagerpro;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.icu.util.LocaleData;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;


public class CreateTaskActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
    public static final String EXTRA_ID = "com.example.todomadeasy.EXTRA_ID";
    public static final String EXTRA_TITLE = "com.example.todomadeasy.EXTRA_TITLE";
    public static final String EXTRA_DESC = "com.example.todomadeasy.EXTRA_DES";
    public static final String EXTRA_TIME = "com.example.todomadeasy.EXTRA_TIME";

    private EditText titleTask;
    private EditText Description;
    private TextView TaskTime;
    public int Mday, Mmonth, Myear, Mhour, Mminute;
    Calendar selectedDate;


    String dateSelected, currentTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_create_task);

        titleTask = findViewById (R.id.title_Task);
        Description = findViewById (R.id.task_Des);
        TaskTime = findViewById (R.id.task_time);
        Button saveTask = findViewById (R.id.SaveTask);
        Button canceltask = findViewById (R.id.Cancel_Task);
        Button showPicker = findViewById (R.id.TimePicker);

        Intent intent = getIntent ();

        //show date and time dialog
        showPicker.setOnClickListener (v -> {
            //default values
            final Calendar instance = Calendar.getInstance ();
            Myear = instance.get (Calendar.YEAR);
            Mmonth = instance.get (Calendar.MONTH);
            Mday = instance.get (Calendar.DAY_OF_MONTH);

            //create an object of datepickerdialog class
            DatePickerDialog datePickerDialog = new DatePickerDialog (CreateTaskActivity.this,
                    CreateTaskActivity.this, Myear, Mmonth, Mday);
            //disable past dates
            datePickerDialog.getDatePicker ().setMinDate (System.currentTimeMillis ()-1000);
            datePickerDialog.show ();
        });

        if (intent.hasExtra (EXTRA_ID)) {

            titleTask.setText (intent.getStringExtra (EXTRA_TITLE));
            Description.setText (intent.getStringExtra (EXTRA_DESC));
            TaskTime.setText (intent.getStringExtra (EXTRA_TIME));
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
        String title = titleTask.getText ().toString ();
        String description = Description.getText ().toString ();
        String time = dateSelected + " at " + currentTime;

        if (TextUtils.isEmpty (title)) {
            titleTask.setError ("please input a Task");
            return;
        }

        if (TextUtils.isEmpty (description)) {
            Description.setError ("write a short description");
            return;
        }
        Intent data = new Intent ();
        data.putExtra (EXTRA_TITLE, title);
        data.putExtra (EXTRA_DESC, description);
        data.putExtra (EXTRA_TIME, time);


        int id = getIntent ().getIntExtra (EXTRA_ID, -1);
        if (id != -1) {
            data.putExtra (EXTRA_ID, id);
        }
        setResult (RESULT_OK, data);
        TaskTime.setText (time);
        finish ();


    }

    private void fireNotification(Calendar targetCal) {
        Intent intent = new Intent (this, AlertReceiver.class);
        String title = titleTask.getText ().toString ();
        String des = Description.getText ().toString ();
        String time = dateSelected + " at " + currentTime;


        intent.putExtra (EXTRA_TITLE, title);
        intent.putExtra (EXTRA_DESC, des);
        intent.putExtra (EXTRA_TIME, time);

        AlarmManager alarmManager = (AlarmManager) getSystemService (ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast (this,
                AlertReceiver.getID (),
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        // start alarm is the selected time is not before the current time
        if(!targetCal.before (Calendar.getInstance ())){
            if (alarmManager != null) {
                alarmManager.setExact (AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis (), pendingIntent);
            }
        }
        else{
            //else cancel its pendingAlarm
            assert alarmManager != null;
            alarmManager.cancel (pendingIntent);

        }


    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        final Calendar c = Calendar.getInstance ();
        c.set (year, month, dayOfMonth);

        selectedDate = c;

        Mhour = c.get (Calendar.HOUR_OF_DAY);
        Mminute = c.get (Calendar.MINUTE);


        dateSelected = java.text.DateFormat.getDateInstance (java.text.DateFormat.SHORT).format (c.getTime ());


        if(isTomorrow (c.getTime ())){
            dateSelected="Tomorrow";
        }

        else if(isToday (c.getTime ())){
            dateSelected="Today";
        }
        else if(isYesterday (c.getTime ())){
            dateSelected="Yesterday";
        }

       else{
           dateSelected = java.text.DateFormat.getDateInstance (java.text.DateFormat.SHORT).format (c.getTime ());
       }


        TimePickerDialog timePickerDialog = new TimePickerDialog (CreateTaskActivity.this,
                CreateTaskActivity.this, Mhour,
                Mminute, android.text.
                format.DateFormat.is24HourFormat (this));


        timePickerDialog.show ();

    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        final Calendar c = Calendar.getInstance ();

        selectedDate.set (c.get (Calendar.YEAR), selectedDate.get (Calendar.MONTH),
                selectedDate.get (Calendar.DAY_OF_MONTH), hourOfDay, minute);

        currentTime = java.text.DateFormat.getTimeInstance (java.text.DateFormat.SHORT).format (selectedDate.getTime ());

        // send notification on selected time/date
        fireNotification (selectedDate);
    }

    public  boolean isYesterday(Date d){
        return DateUtils.isToday (d.getTime ()+DateUtils.DAY_IN_MILLIS);
    }
    public  boolean isTomorrow(Date d){
        return DateUtils.isToday (d.getTime ()-DateUtils.DAY_IN_MILLIS);
    }
    public  boolean isToday(Date d){
        return DateUtils.isToday (d.getTime ());
    }
}
