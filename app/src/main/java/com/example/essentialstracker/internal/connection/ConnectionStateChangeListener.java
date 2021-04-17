package com.example.essentialstracker.internal.connection;


import com.example.essentialstracker.RxBleConnection;

public interface ConnectionStateChangeListener {

    void onConnectionStateChange(RxBleConnection.RxBleConnectionState rxBleConnectionState);
}

