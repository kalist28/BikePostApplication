package com.kalistratov.bikepost.map.gps;

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

    public interface OnUpdateListener {
        void onUpdate();
    }

    private TrackerPointsBase() {
        polylineOptions = new PolylineOptions();
    }

    private static TrackerPointsBase inst;

    public static TrackerPointsBase get() {
        if (inst == null) return (inst = new TrackerPointsBase());
        return inst;
    }

    private final PolylineOptions polylineOptions;
    private OnUpdateListener onUpdateListener;

    public void setOnUpdateListener(final OnUpdateListener onUpdateListener) {
        this.onUpdateListener = onUpdateListener;
    }

    public List<LatLng> getList() {
        return polylineOptions.getPoints();
    }

    public void addPoint(LatLng point) {
        polylineOptions.add(point);
    }

    protected PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void update() {
        if (onUpdateListener == null) return;
        onUpdateListener.onUpdate();
    }

}
