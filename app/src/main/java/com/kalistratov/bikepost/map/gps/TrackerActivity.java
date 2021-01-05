package com.kalistratov.bikepost.map.gps;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.AMapActivity;

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

    private  boolean work = true;
    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrackerPointsBase.get().setOnUpdateListener(() -> {
            if (!mapIsReady) return;

            map.clear();
            map.addPolyline(TrackerPointsBase.get().getPolylineOptions());
        });

        Button button = findViewById(R.id.btn_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                if (work) {
                    startService(new Intent(getBaseContext(), TrackerService.class));
                    Log.e(TAG, "onClick: Start" );
                } else {
                    map.clear();
                    TrackerPointsBase.get().getList().clear();
                    Log.e(TAG, "onClick: Stop" );
                }
                work = !work;
            }
        });


    }

    @Override
    public int viewLayout() {
        return R.layout.activity_maps;
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        super.onMapReady(googleMap);

    }
}
