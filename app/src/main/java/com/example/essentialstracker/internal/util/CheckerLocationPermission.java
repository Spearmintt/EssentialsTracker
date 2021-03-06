package com.example.essentialstracker.internal.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Process;

import com.example.essentialstracker.ClientComponent;
import com.example.essentialstracker.ClientScope;

import javax.inject.Inject;
import javax.inject.Named;

@ClientScope
public class CheckerLocationPermission {

    private final Context context;
    private final String[] scanPermissions;

    @Inject
    CheckerLocationPermission(
            Context context,
            @Named(ClientComponent.PlatformConstants.STRING_ARRAY_SCAN_PERMISSIONS) String[] scanPermissions
    ) {
        this.context = context;
        this.scanPermissions = scanPermissions;
    }

    public boolean isScanRuntimePermissionGranted() {
        for (String locationPermission : scanPermissions) {
            if (isPermissionGranted(locationPermission)) {
                return true;
            }
        }
        return scanPermissions.length == 0;
    }

    public String[] getRecommendedScanRuntimePermissions() {
        return scanPermissions;
    }

    /**
     * Copied from android.support.v4.content.ContextCompat for backwards compatibility
     * @param permission the permission to check
     * @return true is granted
     */
    private boolean isPermissionGranted(String permission) {
        if (permission == null) {
            throw new IllegalArgumentException("permission is null");
        }

        return context.checkPermission(permission, android.os.Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }
}
