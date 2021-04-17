package com.example.essentialstracker.internal.connection;

import com.example.essentialstracker.ConnectionSetup;
import com.example.essentialstracker.RxBleConnection;

import io.reactivex.Observable;

public interface Connector {

    Observable<RxBleConnection> prepareConnection(ConnectionSetup autoConnect);
}
