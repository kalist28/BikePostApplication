package com.kalistratov.bikepost.views;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.fragment.NavHostFragment;

import com.kalistratov.bikepost.R;
import com.kalistratov.bikepost.tools.permissions.PermissionChecker;

/**
 * Class PermissionCheckerActivity.
 *
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version 1.0
 */
public class PermissionCheckerActivity extends AppCompatActivity {

    /**
     * Logging tag.
     */
    private final String TAG = getClass().getSimpleName();

    private static final int REQUEST_CODE_PERMISSION_LOCATION = 1;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_permission_checker);

        Button getPermBtn = findViewById(R.id.getPermissionBtn);
        getPermBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                requestPermissions( new String[] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_LOCATION);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION:
                if (grantResults.length > 0) {
                    boolean result = true;
                    for (int grantResult : grantResults) {
                        result &= grantResult == PackageManager.PERMISSION_GRANTED;
                        Log.e(TAG, "onRequestPermissionsResult: " + result );
                    }
                    if (result) finish();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        boolean FINE_LOCATION = !PermissionChecker.get(this).permGranted(Manifest.permission.ACCESS_FINE_LOCATION);
        boolean COARSE_LOCATION = !PermissionChecker.get(this).permGranted(Manifest.permission.ACCESS_COARSE_LOCATION);

        if (FINE_LOCATION && COARSE_LOCATION) return;

        super.onBackPressed();
    }
}
