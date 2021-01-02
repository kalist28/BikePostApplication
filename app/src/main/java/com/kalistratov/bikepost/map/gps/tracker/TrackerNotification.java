package com.kalistratov.bikepost.map.gps.tracker;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.kalistratov.bikepost.MainActivity;
import com.kalistratov.bikepost.R;

/**
 * Class TrackerNotifiaction.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class TrackerNotification extends NotificationCompat.Builder {

    public static final String CHANNEL_ID = "GPS_Tracker";
    public static final String CHANNEL_NAME = "GPS Tracker";

    public TrackerNotification(@NonNull final Context context) {
        super(context, CHANNEL_ID);

        Intent intent = new Intent( context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);


        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
            setColorized(false);
            setChannelId(CHANNEL_ID);
            //builder.setColor(ContextCompat.getColor(this, R.color.design_default_color_primary));
            setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        }

        setOnlyAlertOnce(true);
        setContentTitle(context.getResources().getString(R.string.app_name));
        //setContentText("Ваш маршрут записывается");
        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION);
        setSound(notificationSound);
        setAutoCancel(true);
        setSmallIcon(R.mipmap.ic_launcher);
        setStyle(new NotificationCompat.BigTextStyle());
        setContentIntent(pendingIntent);
    }
}
