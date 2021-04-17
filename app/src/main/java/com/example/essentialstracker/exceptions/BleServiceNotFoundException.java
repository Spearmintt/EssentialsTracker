package com.example.essentialstracker.exceptions;

import java.util.UUID;

/**
 * An exception emitted from {@link com.example.essentialstracker.RxBleDeviceServices} or {@link com.example.essentialstracker.RxBleConnection}
 * functions that take service's {@link UUID} as a param in case the service with the corresponding UUID is not found in the discovered
 * services.
 */
public class BleServiceNotFoundException extends BleException {

    private final UUID serviceUUID;

    public BleServiceNotFoundException(UUID serviceUUID) {
        super("BLE Service not found with UUID " + serviceUUID);
        this.serviceUUID = serviceUUID;
    }

    public UUID getServiceUUID() {
        return serviceUUID;
    }
}
