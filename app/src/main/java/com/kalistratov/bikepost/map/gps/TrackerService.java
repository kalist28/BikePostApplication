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
import com.kalistratov.bikepost.route.Route;
import com.kalistratov.bikepost.tools.permissions.PermissionChecker;

import java.util.LinkedList;
import java.util.Locale;

import io.realm.Realm;

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
    private static final int INTERVAL_M_SEC = 300;

    /**
     * This method sets the fastest rate in milliseconds
     * at which app can handle location updates.
     */
    private static final int FASTEST_INTERVAL_M_SEC = 500;

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
        if (client != null) {
            client.removeLocationUpdates(locationCallback);
            Log.e(TAG, "Location Update Callback Removed");
        }
        Log.e(TAG, "onDestroy: ");
        notificationManagerCompat.cancel(101);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        int id = realm.where(Route.class).findAll().size() + 1;
        Route route = realm.createObject(Route.class, id);
        route.setRouteName("Маршрут " + id);
        route.setLatLngList(new LinkedList<>(TrackerPointsBase.get().getList()));
        realm.commitTransaction();
        Log.e(TAG, "onDestroy: SAVE" );
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    /** location update action. */
    private void locationUpdateAction (final Location location) {

        final double speed = location.getSpeed() * 3.6;
        final double distance = SphericalUtil.computeLength(TrackerPointsBase.get().getList());

        if (speed >= 1) {
            TrackerPointsBase.get().addPoint(new LatLng(location.getLatitude(), location.getLongitude()));
        }
        TrackerPointsBase.get().update(speed, distance);
        String content = String.format(Locale.ENGLISH, "Скорость:  %.2f км/ч.\nДлинна маршрута: %.2f м.", speed, distance);
        notification.setContentText(content);
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
