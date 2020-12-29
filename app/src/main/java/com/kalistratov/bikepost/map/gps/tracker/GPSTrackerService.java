package com.kalistratov.bikepost.map.gps.tracker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.kalistratov.bikepost.MainActivity;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.LatLngBaseBeta;
import com.kalistratov.bikepost.tools.PermissionChecker;


/**
 * Class GPSTrackerService.
 *
 * A background service that will send data
 * about changes to these locations in broadcast messages.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class GPSTrackerService extends Service {

    /** Logging tag. */
    private final String TAG = getClass().getSimpleName();

    /**
     * The main entry point for interacting with the fused location provider.
     */
    private FusedLocationProviderClient client;

    /**
     * Location update listener.
     * Allows listening to handle location updates.
     */
    private LocationCallback locationCallback;

    /**
     * Unkillable notification.
     * Notifies the user to read his location in real time.
     */
    private NotificationCompat.Builder notificationBuilder;

    /**
     * This method sets the rate in milliseconds
     * at which app prefers to receive location updates.
     */
    private static final int INTERVAL_M_SEC = 500;

    /**
     * This method sets the fastest rate in milliseconds
     * at which app can handle location updates.
     */
    private static final int FASTEST_INTERVAL_M_SEC = 700;


    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "GPS Tracker onCreate :: ");

        createNotification();
        createLocationProvider();

        startForeground(101, notificationBuilder.build());
    }

    @Override
    public void onTaskRemoved(final Intent rootIntent) {
        super.onTaskRemoved(rootIntent);
        Log.e(TAG, "GPS Tracker onTaskRemoved :: ");
    }

    @Override
    public int onStartCommand(final Intent intent,
                              final int flags,
                              final int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.removeLocationUpdates(locationCallback);
            Log.e(TAG, "Location Update Callback Removed");
        }
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    /** location update action. */
    private void locationUpdateAction (final Location location) {
        LatLngBaseBeta.get().getList().add(new LatLng(location.getLatitude(), location.getLongitude()));
        Log.e(TAG, "location size " + LatLngBaseBeta.get().getList().size());
        Log.e(TAG, "location Latitude " + location.getLatitude());
        //Log.e(TAG, "location Longitude " + location.getLongitude());
        //Log.e(TAG, "Speed :: " + location.getSpeed() * 3.6);
    }

    /**
     * Create a provider for the location.
     * Setting an action handler to get a new location.
     */
    @SuppressLint("MissingPermission")
    public void createLocationProvider() {

        LocationRequest request = new LocationRequest();
        request.setFastestInterval(FASTEST_INTERVAL_M_SEC)
                .setInterval(INTERVAL_M_SEC)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        client = LocationServices.getFusedLocationProviderClient(this);

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                if (location == null) return;
                locationUpdateAction(location);
            }
        };

        if (PermissionChecker.get(this).permGranted(Manifest.permission.ACCESS_FINE_LOCATION)
                && PermissionChecker.get(this).permGranted(Manifest.permission.ACCESS_COARSE_LOCATION) ) {
             client.requestLocationUpdates(request, locationCallback, null);
        }
    }

    /** Create and configure an unkillable notification. */
    private void createNotification() {
        Intent intent = new Intent( this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

        String CHANNEL_ID = "GPS_Tracker";
        String CHANNEL_NAME = "GPS_Tracker";

        NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            notificationManager.createNotificationChannel(channel);
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
            notificationBuilder.setColorized(false);
            notificationBuilder.setChannelId(CHANNEL_ID);
            //builder.setColor(ContextCompat.getColor(this, R.color.design_default_color_primary));
            notificationBuilder.setBadgeIconType(NotificationCompat.BADGE_ICON_NONE);
        } else {
            notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID);
        }
        notificationBuilder.setOnlyAlertOnce(true);
        notificationBuilder.setContentTitle(this.getResources().getString(R.string.app_name));
        notificationBuilder.setContentText("Ваш маршрут записывается");
        Uri notificationSound = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_NOTIFICATION);
        notificationBuilder.setSound(notificationSound);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        notificationBuilder.setContentIntent(pendingIntent);
    }

}
