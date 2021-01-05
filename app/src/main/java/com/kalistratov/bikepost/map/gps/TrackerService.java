package com.kalistratov.bikepost.map.gps;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.SphericalUtil;
import com.kalistratov.bikepost.tools.PermissionChecker;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static com.kalistratov.bikepost.map.gps.TrackerNotification.TAG_ACTION;


/**
 * Class GPSTrackerService.
 *
 * A background service that will send data
 * about changes to these locations in broadcast messages.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class TrackerService extends Service {

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
    private TrackerNotification notification;

    /**
     * System notification manager.
     */
    private NotificationManager notificationManagerCompat;

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

        notification = new TrackerNotification(this);
        createLocationProvider();

        notificationManagerCompat = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        startForeground(101, notification.build());
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
        Log.e(TAG, "onStartCommand: " + intent );
        try {
            if (intent.getStringExtra("action").equals(TAG_ACTION)) {
                Log.e(TAG, "onStartCommand: stop service");
                stopSelf();
            }
        } catch (NullPointerException e) {
            Log.e(TAG, "Location " + e);
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (client != null) {
            client.removeLocationUpdates(locationCallback);
            Log.e(TAG, "Location Update Callback Removed");
        }
        Log.e(TAG, "onDestroy: ");
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    /** location update action. */
    private void locationUpdateAction (final Location location) {
        Log.e(TAG, "location size " + TrackerPointsBase.get().getList().size());
        Log.e(TAG, "location Latitude " + location.getLatitude());
        //Log.e(TAG, "location Longitude " + location.getLongitude());
        Log.e(TAG, "Speed :: " + location.getSpeed() * 3.6);

        final double speed = location.getSpeed() * 3.6;
        if (speed >= 0.6) {
            TrackerPointsBase.get().addPoint(new LatLng(location.getLatitude(), location.getLongitude()));
            TrackerPointsBase.get().update();
        }
        double distance = SphericalUtil.computeLength(TrackerPointsBase.get().getList());
        notification.setContentText("Speed : " + speed + "\nДлинна маршрута : " + distance);

        notificationManagerCompat.notify(101, notification.build());

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
}
