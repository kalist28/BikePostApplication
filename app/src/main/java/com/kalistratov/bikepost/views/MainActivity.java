package com.kalistratov.bikepost.views;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.map.gps.TrackerActivity;
import com.kalistratov.bikepost.tools.permissions.PermissionChecker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;

import android.view.Menu;
import android.view.MenuItem;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        boolean FINE_LOCATION = !PermissionChecker.get(this).permGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean COARSE_LOCATION = !PermissionChecker.get(this).permGranted(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (FINE_LOCATION && COARSE_LOCATION) startActivity(new Intent(this, PermissionCheckerActivity.class));

        String action = getIntent().getStringExtra("action");
        checkAction(action);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();

                startActivity(new Intent(MainActivity.this,  TrackerActivity.class));
            }
        });
    }

    private void checkAction(final String action) {
        if (action == null) return;
        if(action.equals("openTracker")){
            startActivity(new Intent(this, TrackerActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}