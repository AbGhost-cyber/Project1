package com.example.taskmanagerpro;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import java.util.Date;

public class AlertReceiver extends BroadcastReceiver {
    static int id = getID ();

    @Override
    public void onReceive(Context context, Intent intent) {
        //get intent extras from CreatTaskActivity
        String title = intent.getStringExtra (CreateTaskActivity.EXTRA_TITLE);
        String description = intent.getStringExtra (CreateTaskActivity.EXTRA_DESC);
        String time = intent.getStringExtra (CreateTaskActivity.EXTRA_TIME);
        taskNotificationHelper b = new taskNotificationHelper (context);
        NotificationCompat.Builder builder = b.ScheduleNotification (title, description, time);
        b.getManager ().notify (id, builder.build ());

    }

    //generates unique integer id for every notification
    public static int getID() {
        return (int) ((new Date ().getTime () / 1000L) % Integer.MAX_VALUE);
    }

    //notification helper class
    public static class taskNotificationHelper extends ContextWrapper {
        public static final String TaskChannel1_ID = "taskChannel1ID";
        public static final String TaskChannel1_Name = "Task Alarm";
        public static final String TITLE = "com.example.taskmanagerpro.TITLE";
        public static final String DESC = "com.example.taskmanagerpro.DESC";
        public static final String TIME = "com.example.taskmanagerpro.TIME";

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

        public NotificationCompat.Builder ScheduleNotification(String title, String description, String time) {


            Intent Notificationintent = new Intent (this, SendToComplete.class);

            Notificationintent.addFlags (Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

            Notificationintent.putExtra (TITLE, title);
            Notificationintent.putExtra (DESC, description);
            Notificationintent.putExtra (TIME, time);

            PendingIntent resultPendingIntent = PendingIntent.getActivity (this, id
                    , Notificationintent,
                    PendingIntent.FLAG_ONE_SHOT);
            id++;


            return new NotificationCompat.Builder (this, TaskChannel1_ID)
                    .setContentTitle ("Right on time!")
                    .setContentText ("it's time to " + title + " Let's Go!")
                    .setColor (getResources ().getColor (R.color.colorAccent))
                    .setSmallIcon (R.drawable.ic_alarm)
                    .setAutoCancel (true)
                    .setContentIntent (resultPendingIntent)
                    .setPriority (NotificationCompat.PRIORITY_HIGH);


        }

    }


}




