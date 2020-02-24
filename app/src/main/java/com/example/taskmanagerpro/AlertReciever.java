package com.example.taskmanagerpro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;

import androidx.core.app.NotificationCompat;

public class AlertReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        CreateTaskActivity.taskNotificationHelper b= new CreateTaskActivity.taskNotificationHelper (context);
        NotificationCompat.Builder builder=b.getChannelNotification ();
        b.getManager ().notify (1,builder.build ());
    }



}
