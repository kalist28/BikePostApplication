package com.kalistratov.bikepost.route;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.kalistratov.bikepost.R;

import java.util.LinkedList;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Class RoutesActivity.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class RoutesActivity extends FragmentActivity {

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new RouteRecyclerAdapter());
    }
}
