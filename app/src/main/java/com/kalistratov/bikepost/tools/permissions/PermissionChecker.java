package com.kalistratov.bikepost.tools.permissions;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;

/**
 * Class PermissionChecker.
 *
 * Tool for checking and obtaining user permissions.
 * @author Dmitry Kalistratov <dmitry@kalistratov.ru>
 * @version %I%, %G%
 */
public final class PermissionChecker {
    /** Working context. */
    private final Context context;

    /**
     * Get new instance.
     * @param context - working context.
     * @return a new object in context.
     */
    public static PermissionChecker get(final Context context) {
        return new PermissionChecker(context);
    }

    /**
     * Private constructor.
     * @param context working context.
     */
    private PermissionChecker(final Context context) {
        this.context = context;
    }

    /**
     * Checking permission granted.
     * @param permission - permission to check.
     * @return true at the available resolution.
     */
    public final boolean permGranted(final String permission) {
        int res = ActivityCompat
                .checkSelfPermission(
                        context,
                        permission
                );
        return res == PackageManager.PERMISSION_GRANTED;
    }
}
