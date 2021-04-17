package com.example.essentialstracker.internal.scan;

import androidx.annotation.RestrictTo;

import com.example.essentialstracker.internal.RxBleDeviceProvider;
import com.example.essentialstracker.scan.ScanResult;

import javax.inject.Inject;

import io.reactivex.functions.Function;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class InternalToExternalScanResultConverter implements Function<RxBleInternalScanResult, ScanResult> {

    private final RxBleDeviceProvider deviceProvider;

    @Inject
    public InternalToExternalScanResultConverter(RxBleDeviceProvider deviceProvider) {
        this.deviceProvider = deviceProvider;
    }

    @Override
    public ScanResult apply(RxBleInternalScanResult rxBleInternalScanResult) {
        return new ScanResult(
                deviceProvider.getBleDevice(rxBleInternalScanResult.getBluetoothDevice().getAddress()),
                rxBleInternalScanResult.getRssi(),
                rxBleInternalScanResult.getTimestampNanos(),
                rxBleInternalScanResult.getScanCallbackType(),
                rxBleInternalScanResult.getScanRecord()
        );
    }
}