package com.example.essentialstracker.internal.connection;

import android.bluetooth.BluetoothGattCharacteristic;

import com.example.essentialstracker.internal.BluetoothGattCharacteristicProperty;
import com.example.essentialstracker.internal.logger.LoggerUtil;
import com.example.essentialstracker.internal.util.CharacteristicPropertiesParser;

import java.util.Locale;

import javax.inject.Inject;

public class IllegalOperationMessageCreator {

    private final CharacteristicPropertiesParser propertiesParser;

    @Inject
    public IllegalOperationMessageCreator(CharacteristicPropertiesParser propertiesParser) {
        this.propertiesParser = propertiesParser;
    }

    @SuppressWarnings("WrongConstant")
    public String createMismatchMessage(BluetoothGattCharacteristic characteristic,
                                        @BluetoothGattCharacteristicProperty int neededProperties) {
        return String.format(
                Locale.getDefault(),
                "Characteristic %s supports properties: %s (%d) does not have any property matching %s (%d)",
                LoggerUtil.getUuidToLog(characteristic.getUuid()),
                propertiesParser.propertiesIntToString(characteristic.getProperties()),
                characteristic.getProperties(),
                propertiesParser.propertiesIntToString(neededProperties),
                neededProperties
        );
    }
}