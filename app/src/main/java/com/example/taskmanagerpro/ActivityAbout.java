package com.example.taskmanagerpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.app.ActionBar;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

public class ActivityAbout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.about_app);
        TextView textView=findViewById (R.id.paragraph);
        String setParagraph="Task manager pro was created to help it's users organize their daily schedules, because we may forget our " +
                " initial plan for a specific time, so Task manager pro helps the user to schedule a particular time and set an alarm for it so that the user " +
                " can be notified when the time reaches and also display a list of completed schedules.  Created by Abundance(RAY).";
        textView.setText (setParagraph);
        TextView howToUse=findViewById (R.id.howToUse);
        String setHowToUse="to create a new schedule/task, click on the floating button  +  at the bottom " +
                "right of the home screen and insert your schedule info. To open the time dialog, click on time picker button in the create task screen." +
                " To show the list of completed schedules/tasks, click on the com-task icon(a schedule is only completed when you're notified of the " +
                "incoming schedule),you can choose to complete the task or cancel the alarm.";
        howToUse.setText (setHowToUse);
        getSupportActionBar ().setTitle ("About Task Manager Pro");
            getSupportActionBar ().setDisplayHomeAsUpEnabled (true);//add back button manually
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId () == android.R.id.home) {//if back button is clicked
            finish ();
            return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

}
