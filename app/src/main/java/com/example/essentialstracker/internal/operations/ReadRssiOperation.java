package com.example.essentialstracker.internal.operations;

import android.bluetooth.BluetoothGatt;

import com.example.essentialstracker.exceptions.BleGattOperationType;
import com.example.essentialstracker.internal.SingleResponseOperation;
import com.example.essentialstracker.internal.connection.ConnectionModule;
import com.example.essentialstracker.internal.connection.RxBleGattCallback;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;

public class ReadRssiOperation extends SingleResponseOperation<Integer> {

    @Inject
    ReadRssiOperation(RxBleGattCallback bleGattCallback, BluetoothGatt bluetoothGatt,
                      @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration) {
        super(bluetoothGatt, bleGattCallback, BleGattOperationType.READ_RSSI, timeoutConfiguration);
    }

    @Override
    protected Single<Integer> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback.getOnRssiRead().firstOrError();
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.readRemoteRssi();
    }

    @Override
    public String toString() {
        return "ReadRssiOperation{" + super.toString() + '}';
    }
}
