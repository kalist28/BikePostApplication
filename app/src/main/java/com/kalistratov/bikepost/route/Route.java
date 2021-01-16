package com.kalistratov.bikepost.route;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Class Route.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class Route extends RealmObject {

    /**
     * Logging tag.
     */
    @Ignore private final transient String TAG = getClass().getSimpleName();
    @Ignore private static transient Gson gson = new Gson();

    @Ignore private transient LinkedList<LatLng> latLngList;
    @Ignore private transient Date dateObject;


    @PrimaryKey
    private long id;

    private String uId;

    private String routeName;

    private String date;

    private String serializeList;

    @SuppressLint("SimpleDateFormat")
    public Route() {

        if (uId == null) {
            uId = UUID.randomUUID().toString();
        }

        if (serializeList != null) {
            latLngList = gson.fromJson(serializeList,
                    new TypeToken<LinkedList<LatLng>>() {}.getType());
        }

        if (date == null) {
            dateObject = new Date();
            date = gson.toJson(dateObject);
        } else {
            dateObject = gson.fromJson(date, Date.class);
        }
    }


    public long getId() {
        return id;
    }

    public void setId(final long id) {
        this.id = id;
    }

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(final String routeName) {
        this.routeName = routeName;
    }

    public String getuId() {
        return uId;
    }

    public Date getDate() {
        return dateObject;
    }

    public List<LatLng> getLatLngList() {
        return latLngList;
    }

    public void setLatLngList(final LinkedList<LatLng> latLngList) {
        this.latLngList = latLngList;
        serializeList = gson.toJson(latLngList);
    }

    @Override
    public String toString() {
        return "Route{" +
                "TAG='" + TAG + '\'' +
                ", id=" + id +
                ", uId='" + uId + '\'' +
                ", routeName='" + routeName + '\'' +
                ", date='" + date + '\'' +
                ", serializeList='" + serializeList + '\'' +
                ", latLngList=" + latLngList +
                '}';
    }
}
