package com.example.essentialstracker.internal.connection;

import android.bluetooth.BluetoothGattCharacteristic;

import com.example.essentialstracker.internal.BleIllegalOperationException;
import com.example.essentialstracker.internal.RxBleLog;

import javax.inject.Inject;

/**
 * Implementation of {@link IllegalOperationHandler}. This class logs a warning if there was no match between possessed
 * and requested properties.
 */
public class LoggingIllegalOperationHandler extends IllegalOperationHandler {

    @Inject
    public LoggingIllegalOperationHandler(IllegalOperationMessageCreator messageCreator) {
        super(messageCreator);
    }

    /**
     * This method logs a warning.
     * @param characteristic the characteristic upon which the operation was requested
     * @param neededProperties bitmask of properties needed by the operation
     */
    @Override
    public BleIllegalOperationException handleMismatchData(BluetoothGattCharacteristic characteristic, int neededProperties) {
        RxBleLog.w(messageCreator.createMismatchMessage(characteristic, neededProperties));
        return null;
    }
}
