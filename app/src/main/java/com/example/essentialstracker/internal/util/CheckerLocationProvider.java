package com.example.essentialstracker.internal.util;

import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;

import com.example.essentialstracker.internal.RxBleLog;

import javax.inject.Inject;

@TargetApi(19 /* Build.VERSION_CODES.KITKAT */)
public class CheckerLocationProvider {

    private final ContentResolver contentResolver;
    private final LocationManager locationManager;

    @Inject
    CheckerLocationProvider(ContentResolver contentResolver, LocationManager locationManager) {
        this.contentResolver = contentResolver;
        this.locationManager = locationManager;
    }

    @SuppressWarnings("deprecation")
    public boolean isLocationProviderEnabled() {
        if (Build.VERSION.SDK_INT >= 19 /* Build.VERSION_CODES.KITKAT */) {
            try {
                return Settings.Secure.getInt(contentResolver, Settings.Secure.LOCATION_MODE) != Settings.Secure.LOCATION_MODE_OFF;
            } catch (Settings.SettingNotFoundException e) {
                RxBleLog.w(e, "Could not use LOCATION_MODE check. Falling back to legacy method.");
            }
        }
        return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }
}
