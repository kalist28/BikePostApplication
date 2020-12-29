package com.kalistratov.bikepost.map;

import com.google.android.gms.maps.model.LatLng;

import java.util.LinkedList;

/**
 * Class LatLngBaseBeta.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class LatLngBaseBeta {

    private LinkedList<LatLng> latLngs;
    private LatLngBaseBeta() {
        latLngs = new LinkedList<>();
    }

    private static LatLngBaseBeta inst;

    public static LatLngBaseBeta get() {
        if (inst == null) return (inst = new LatLngBaseBeta());
        return inst;
    }


    public LinkedList<LatLng> getList() {
        return latLngs;
    }
}
