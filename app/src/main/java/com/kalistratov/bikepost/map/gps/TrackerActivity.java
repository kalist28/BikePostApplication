package com.kalistratov.bikepost.map.gps;

import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.material.button.MaterialButton;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.AMapActivity;
import com.shreyaspatil.MaterialDialog.AbstractDialog;
import com.shreyaspatil.MaterialDialog.BottomSheetMaterialDialog;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;
import com.shreyaspatil.MaterialDialog.interfaces.OnCancelListener;

import java.util.Locale;

/**
 * Class LibLocUpdateActivity.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class TrackerActivity extends AMapActivity {

    /**
     * Logging tag.
     */
    private final String TAG = getClass().getSimpleName();
    private BottomSheetMaterialDialog mBottomSheetDialog;
    private Intent service;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new Intent(getBaseContext(), TrackerService.class);

        TextView textView = findViewById(R.id.trackInfo);
        Button stopBtn = findViewById(R.id.stopBtn);

        TrackerPointsBase.get().setOnUpdateListener((speed, distance) -> {
            if (!mapIsReady) return;

            map.clear();
            map.addPolyline(TrackerPointsBase.get().getPolylineOptions());

            String content = String.format(Locale.ENGLISH, "Скорость:  %.2f км/ч.\nПуть:  %.2f м.", speed, distance);
            textView.setText(content);
            mBottomSheetDialog.dismiss();
        });

        stopBtn.setOnClickListener(v -> stopTrackerService());
    }

    @Override
    protected void onResume() {
        super.onResume();
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);

        mBottomSheetDialog = dialogBuilder(gpsEnabled).build();

        mBottomSheetDialog.setOnCancelListener(dialogInterface -> {
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            finish();
        });

        mBottomSheetDialog.show();

        if (gpsEnabled) {
            moveCameraToLocation();
            startTrackerService();
        }
    }

    /** Create a dialog depending on the phone settings. */
    private BottomSheetMaterialDialog.Builder dialogBuilder(final boolean gpsEnabled) {
        String sheetContent = gpsEnabled ? "Загрузка карты." : "Для записи маршрута требуется геолокация.\nОткройте настройки и включите GPS или закройте треккер.";
        BottomSheetMaterialDialog.Builder builder = new BottomSheetMaterialDialog.Builder(this)
                .setTitle("Загрузка")
                .setMessage(sheetContent)
                .setAnimation("bike_loading_animation.json");

        if (!gpsEnabled) {
            builder.setPositiveButton("Открыть", R.drawable.ic_baseline_gps_fixed_24,  new BottomSheetMaterialDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    mBottomSheetDialog.dismiss();
                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                }
            });
            builder.setNegativeButton("Закрыть", R.drawable.ic_baseline_close_24,  new BottomSheetMaterialDialog.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int which) {
                    finish();
                }
            });
        }
        return builder;
    }

    /** Tracking service startup. */
    private void startTrackerService() {
        startService(service);
    }

    /** Tracking service stop. */
    private void stopTrackerService() {
        stopService(service);
    }

    private int PxToDp(final int px) {
        return px / (getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @Override
    public int viewLayout() {
        return R.layout.activity_tracker;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        super.onMapReady(googleMap);
        map.setPadding(0, 0, 0, PxToDp(350));
    }
}
