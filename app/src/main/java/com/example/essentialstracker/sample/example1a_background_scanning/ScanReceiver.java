package com.example.essentialstracker.sample.example1a_background_scanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.example.essentialstracker.exceptions.BleScanException;
import com.example.essentialstracker.sample.SampleApplication;
import com.example.essentialstracker.scan.BackgroundScanner;
import com.example.essentialstracker.scan.ScanResult;

import java.util.List;

public class ScanReceiver extends BroadcastReceiver {

    @RequiresApi(26 /* Build.VERSION_CODES.O */)
    @Override
    public void onReceive(Context context, Intent intent) {
        BackgroundScanner backgroundScanner = SampleApplication.getRxBleClient(context).getBackgroundScanner();

        try {
            final List<ScanResult> scanResults = backgroundScanner.onScanResultReceived(intent);
            Log.i("ScanReceiver", "Scan results received: " + scanResults);
        } catch (BleScanException exception) {
            Log.w("ScanReceiver", "Failed to scan devices", exception);
        }
    }
}
