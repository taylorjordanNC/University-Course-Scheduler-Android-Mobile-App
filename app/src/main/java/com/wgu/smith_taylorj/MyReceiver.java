package com.wgu.smith_taylorj;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

public class MyReceiver extends BroadcastReceiver {
    static int notificationID;
    String channel_id = "test";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, intent.getStringExtra("toastMsg"), Toast.LENGTH_LONG).show();
        createNotificationChannel(context, channel_id);
        Notification n = new NotificationCompat.Builder(context, channel_id)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentText(intent.getStringExtra("toastMsg"))
                .setContentTitle("Degree Program Alert").build();

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationID++, n);

        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // throw new UnsupportedOperationException("Not yet implemented");
    }

    private void createNotificationChannel(Context context, String CHANNEL_ID){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "channel";
            String desc = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(desc);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}