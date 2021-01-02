package com.kalistratov.bikepost.map.gps.tracker;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Class LatLngBaseBeta.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class TrackerPointsBase {

    private TrackerPointsBase() {
        polylineOptions = new PolylineOptions();
        Log.e("TFDFGFDGD", "created");
    }

    private static TrackerPointsBase inst;

    public static TrackerPointsBase get() {
        if (inst == null) return (inst = new TrackerPointsBase());
        return inst;
    }

    private PolylineOptions polylineOptions;

    public List<LatLng> getList() {
        return polylineOptions.getPoints();
    }

    public void addPoint(LatLng point) {
        polylineOptions.add(point);
    }

    protected PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }
}
