package com.kalistratov.bikepost.map;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.gps.tracker.GPSTrackerService;

/**
 * Class LibLocUpdateActivity.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class LibLocUpdateActivity extends AMapActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        startService(new Intent(this, GPSTrackerService.class));

        Button button = findViewById(R.id.btn_start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                map.clear();
                PolylineOptions polyline = new PolylineOptions();
                for (LatLng l : LatLngBaseBeta.get().getList()) {
                    polyline.add(l);
                }
                map.addPolyline(polyline);
                LatLngBaseBeta.get().getList().clear();
            }
        });
    }

    @Override
    public int viewLayout() {
        return R.layout.activity_maps;
    }
}
