package com.kalistratov.bikepost.route;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.maps.android.SphericalUtil;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.gps.TrackerPointsBase;

import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class RouteRecyclerAdapter.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class RouteRecyclerAdapter extends RecyclerView.Adapter<RouteRecyclerAdapter.Holder> {

    /**
     * Logging tag.
     */
    private final String TAG = getClass().getSimpleName();

    private Realm realm;
    private RealmResults<Route> results;

    public RouteRecyclerAdapter() {
        realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        results = realm.where(Route.class).findAll();
        Log.e(TAG, "RouteRecyclerAdapter: " + results.size() );
        realm.cancelTransaction();
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_card_route, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final Holder holder, final int position) {
        Route route = results.get(position);
        holder.topic.setText(route.getRouteName());
        holder.date.setText(new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH).format(route.getDate()));
        final double distance = SphericalUtil.computeLength(route.getLatLngList());
        holder.distance.setText(String.format(Locale.ENGLISH,"Дистанция %.2f м.", distance));
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public static class Holder extends RecyclerView.ViewHolder {
        protected CardView card;
        protected TextView topic;
        protected TextView date;
        protected TextView distance;
        public Holder(@NonNull final View v) {
            super(v);
            topic       = v.findViewById(R.id.routeTopic);
            date        = v.findViewById(R.id.routeDate);
            distance    = v.findViewById(R.id.routeLength);
        }
    }
}
