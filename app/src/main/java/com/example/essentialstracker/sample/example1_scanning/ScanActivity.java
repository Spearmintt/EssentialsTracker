package com.example.essentialstracker.sample.example1_scanning;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.essentialstracker.R;
import com.example.essentialstracker.RxBleClient;
import com.example.essentialstracker.exceptions.BleScanException;
import com.example.essentialstracker.sample.DeviceActivity;
import com.example.essentialstracker.sample.SampleApplication;
import com.example.essentialstracker.sample.example1a_background_scanning.BackgroundScanActivity;
import com.example.essentialstracker.sample.util.LocationPermission;
import com.example.essentialstracker.sample.util.ScanExceptionHandler;
import com.example.essentialstracker.scan.ScanFilter;
import com.example.essentialstracker.scan.ScanResult;
import com.example.essentialstracker.scan.ScanSettings;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;

public class ScanActivity extends AppCompatActivity {

    @BindView(R.id.scan_toggle_btn)
    Button scanToggleButton;
    @BindView(R.id.scan_results)
    RecyclerView recyclerView;
    private RxBleClient rxBleClient;
    private Disposable scanDisposable;
    private ScanResultsAdapter resultsAdapter;
    private boolean hasClickedScan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example1);
        ButterKnife.bind(this);
        rxBleClient = SampleApplication.getRxBleClient(this);
        configureResultList();
    }

    @OnClick(R.id.background_scan_btn)
    public void onBackgroundScanRequested() {
        startActivity(new Intent(this, BackgroundScanActivity.class));
    }

    @OnClick(R.id.scan_toggle_btn)
    public void onScanToggleClick() {

        if (isScanning()) {
            scanDisposable.dispose();
        } else {
            if (rxBleClient.isScanRuntimePermissionGranted()) {
                scanBleDevices();
            } else {
                hasClickedScan = true;
                LocationPermission.requestLocationPermission(this, rxBleClient);
            }
        }

        updateButtonUIState();
    }

    private void scanBleDevices() {
        scanDisposable = rxBleClient.scanBleDevices(
                new ScanSettings.Builder()
                        .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                        .setCallbackType(ScanSettings.CALLBACK_TYPE_ALL_MATCHES)
                        .build(),
                new ScanFilter.Builder()
//                            .setDeviceAddress("B4:99:4C:34:DC:8B")
                        // add custom filters if needed
                        .build()
        )
                .observeOn(AndroidSchedulers.mainThread())
                .doFinally(this::dispose)
                .subscribe(resultsAdapter::addScanResult, this::onScanFailure);
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions,
                                           @NonNull final int[] grantResults) {
        if (LocationPermission.isRequestLocationPermissionGranted(requestCode, permissions, grantResults, rxBleClient)
                && hasClickedScan) {
            hasClickedScan = false;
            scanBleDevices();
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        if (isScanning()) {
            /*
             * Stop scanning in onPause callback.
             */
            scanDisposable.dispose();
        }
    }

    private void configureResultList() {
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(null);
        LinearLayoutManager recyclerLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);
        resultsAdapter = new ScanResultsAdapter();
        recyclerView.setAdapter(resultsAdapter);
        resultsAdapter.setOnAdapterItemClickListener(view -> {
            final int childAdapterPosition = recyclerView.getChildAdapterPosition(view);
            final ScanResult itemAtPosition = resultsAdapter.getItemAtPosition(childAdapterPosition);
            onAdapterItemClick(itemAtPosition);
        });
    }

    private boolean isScanning() {
        return scanDisposable != null;
    }

    private void onAdapterItemClick(ScanResult scanResults) {
        final String macAddress = scanResults.getBleDevice().getMacAddress();
        final Intent intent = new Intent(this, DeviceActivity.class);
        intent.putExtra(DeviceActivity.EXTRA_MAC_ADDRESS, macAddress);
        startActivity(intent);
    }

    private void onScanFailure(Throwable throwable) {
        if (throwable instanceof BleScanException) {
            ScanExceptionHandler.handleException(this, (BleScanException) throwable);
        }
    }

    private void dispose() {
        scanDisposable = null;
        resultsAdapter.clearScanResults();
        updateButtonUIState();
    }

    private void updateButtonUIState() {
        scanToggleButton.setText(isScanning() ? "Stop Scan" : "Start Scan");
    }
}
