package com.example.essentialstracker.internal.operations;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.example.essentialstracker.exceptions.BleGattOperationType;
import com.example.essentialstracker.internal.SingleResponseOperation;
import com.example.essentialstracker.internal.connection.ConnectionModule;
import com.example.essentialstracker.internal.connection.RxBleGattCallback;
import com.example.essentialstracker.internal.logger.LoggerUtil;

import javax.inject.Named;
import io.reactivex.Single;

import static com.example.essentialstracker.internal.util.ByteAssociationUtil.characteristicUUIDPredicate;
import static com.example.essentialstracker.internal.util.ByteAssociationUtil.getBytesFromAssociation;

public class CharacteristicWriteOperation extends SingleResponseOperation<byte[]> {

    private final BluetoothGattCharacteristic bluetoothGattCharacteristic;
    private final byte[] data;

    CharacteristicWriteOperation(RxBleGattCallback rxBleGattCallback, BluetoothGatt bluetoothGatt,
                                 @Named(ConnectionModule.OPERATION_TIMEOUT) TimeoutConfiguration timeoutConfiguration,
                                 BluetoothGattCharacteristic bluetoothGattCharacteristic,
                                 byte[] data) {
        super(bluetoothGatt, rxBleGattCallback, BleGattOperationType.CHARACTERISTIC_WRITE, timeoutConfiguration);
        this.bluetoothGattCharacteristic = bluetoothGattCharacteristic;
        this.data = data;
    }

    @Override
    protected Single<byte[]> getCallback(RxBleGattCallback rxBleGattCallback) {
        return rxBleGattCallback
                .getOnCharacteristicWrite()
                .filter(characteristicUUIDPredicate(bluetoothGattCharacteristic.getUuid()))
                .firstOrError()
                .map(getBytesFromAssociation());
    }

    @Override
    protected boolean startOperation(BluetoothGatt bluetoothGatt) {
        bluetoothGattCharacteristic.setValue(data);
        return bluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
    }

    @Override
    public String toString() {
        return "CharacteristicWriteOperation{"
                + super.toString()
                + ", characteristic=" + new LoggerUtil.AttributeLogWrapper(bluetoothGattCharacteristic.getUuid(), data, true)
                + '}';
    }
}
