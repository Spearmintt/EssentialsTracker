package com.example.essentialstracker.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattDescriptor;

import com.example.essentialstracker.exceptions.BleGattOperationType;
import com.example.essentialstracker.internal.SingleResponseOperation;
import com.example.essentialstracker.internal.connection.ConnectionModule;
import com.example.essentialstracker.internal.connection.RxBleGattCallback;
import com.example.essentialstracker.internal.logger.LoggerUtil;
import com.example.essentialstracker.internal.util.ByteAssociation;

import javax.inject.Inject;
import javax.inject.Named;

import io.reactivex.Single;

import static com.example.essentialstracker.internal.util.ByteAssociationUtil.descriptorPredicate;

public class DescriptorReadOperation extends SingleResponseOperation<ByteAssociation<BluetoothGattDescriptor>> {

    private final BluetoothGattDescriptor bluetoothGattDescriptor;

    @Inject
    DescriptorReadOperation(RxBleGattCallback rxBleGattCallback, BluetoothGatt bluetoothGatt,
                            @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                            BluetoothGattDescriptor descriptor) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.DESCRIPTOR_READ, timeoutConfiguration);
        bluetoothGattDescriptor = descriptor;
    }

    @Override
    protected Single<ByteAssociation<BluetoothGattDescriptor>> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnDescriptorRead()
                .filter(descriptorPredicate(bluetoothGattDescriptor))
                .firstOrError();
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        return bluetoothGatt.readDescriptor(bluetoothGattDescriptor);
    }

    @Override
    public String toString() {
        return "DescriptorReadOperation{"
                + super.toString()
                + ", descriptor=" + LoggerUtil.wrap(bluetoothGattDescriptor, false)
                + '}';
    }
}
