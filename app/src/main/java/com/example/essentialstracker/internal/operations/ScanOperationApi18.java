package com.example.essentialstracker.internal.operations;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import androidx.annotation.NonNull;

import com.example.essentialstracker.LogConstants;
import com.example.essentialstracker.internal.RxBleLog;
import com.example.essentialstracker.internal.logger.LoggerUtil;
import com.example.essentialstracker.internal.scan.EmulatedScanFilterMatcher;
import com.example.essentialstracker.internal.scan.InternalScanResultCreator;
import com.example.essentialstracker.internal.scan.RxBleInternalScanResult;
import com.example.essentialstracker.internal.util.RxBleAdapterWrapper;

import io.reactivex.ObservableEmitter;

public class ScanOperationApi18 extends ScanOperation<RxBleInternalScanResult, BluetoothAdapter.LeScanCallback> {

    @NonNull
    final InternalScanResultCreator scanResultCreator;
    @NonNull
    final EmulatedScanFilterMatcher scanFilterMatcher;

    public ScanOperationApi18(
            @NonNull RxBleAdapterWrapper rxBleAdapterWrapper,
            @NonNull final InternalScanResultCreator scanResultCreator,
            @NonNull final EmulatedScanFilterMatcher scanFilterMatcher
    ) {

        super(rxBleAdapterWrapper);
        this.scanResultCreator = scanResultCreator;
        this.scanFilterMatcher = scanFilterMatcher;
    }

    @Override
    BluetoothAdapter.LeScanCallback createScanCallback(final ObservableEmitter<RxBleInternalScanResult> emitter) {
        return new BluetoothAdapter.LeScanCallback() {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                if (!scanFilterMatcher.isEmpty() && RxBleLog.isAtLeast(LogConstants.DEBUG) && RxBleLog.getShouldLogScannedPeripherals()) {
                    RxBleLog.d("%s, name=%s, rssi=%d, data=%s",
                            LoggerUtil.commonMacMessage(device.getAddress()),
                            device.getName(),
                            rssi,
                            LoggerUtil.bytesToHex(scanRecord)
                    );
                }
                final RxBleInternalScanResult internalScanResult = scanResultCreator.create(device, rssi, scanRecord);
                if (scanFilterMatcher.matches(internalScanResult)) {
                    emitter.onNext(internalScanResult);
                }
            }
        };
    }

    @Override
    boolean startScan(RxBleAdapterWrapper rxBleAdapterWrapper, BluetoothAdapter.LeScanCallback scanCallback) {
        if (this.scanFilterMatcher.isEmpty()) {
            RxBleLog.d("No library side filtering —> debug logs of scanned devices disabled");
        }
        return rxBleAdapterWrapper.startLegacyLeScan(scanCallback);
    }

    @Override
    void stopScan(RxBleAdapterWrapper rxBleAdapterWrapper, BluetoothAdapter.LeScanCallback scanCallback) {
        // TODO: [PU] 29.01.2016 https://code.google.com/p/android/issues/detail?id=160503
        rxBleAdapterWrapper.stopLegacyLeScan(scanCallback);
    }

    @Override
    public String toString() {
        return "ScanOperationApi18{"
                + (scanFilterMatcher.isEmpty() ? "" : "ANY_MUST_MATCH -> " + scanFilterMatcher)
                + '}';
    }
}
