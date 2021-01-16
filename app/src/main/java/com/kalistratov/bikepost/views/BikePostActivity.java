package com.kalistratov.bikepost.views;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.kalistratov.bikepost.R;

public class BikePostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bike_post);
        Toast.makeText(
                this,
                getIntent().getStringExtra("Name"),
                Toast.LENGTH_LONG
        ).show();
    }
}