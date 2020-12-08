package com.kalistratov.bikepost.entitys;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.annotations.SerializedName;

public class BikePost extends Entity<BikePost> {
    @SerializedName("name")
    public String name;

    @SerializedName("coordinates")
    public String coordinates;

    public double getLat() {
        return Double.parseDouble(coordinates.substring(0, coordinates.indexOf(',')));
    }

    public double getLng() {
        return Double.parseDouble(coordinates.substring(coordinates.indexOf(',') + 1));
    }

    public LatLng getCoordinates() {
        return new LatLng(getLat(), getLng());
    }

    @Override
    public String toString() {
        return "BikePost{" +
                "name='" + name + '\'' +
                ", coordinates='" + getLat() + ":" + getLng() + '\'' +
                '}';
    }
}

