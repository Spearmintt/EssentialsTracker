package com.example.essentialstracker.internal;

import android.bluetooth.BluetoothDevice;
import androidx.annotation.Nullable;

import com.jakewharton.rxrelay2.BehaviorRelay;
import com.example.essentialstracker.ConnectionSetup;
import com.example.essentialstracker.RxBleConnection;
import com.example.essentialstracker.RxBleDevice;
import com.example.essentialstracker.Timeout;
import com.example.essentialstracker.exceptions.BleAlreadyConnectedException;
import com.example.essentialstracker.internal.connection.Connector;

import com.example.essentialstracker.internal.logger.LoggerUtil;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.functions.Action;

@DeviceScope
class RxBleDeviceImpl implements RxBleDevice {

    final BluetoothDevice bluetoothDevice;
    final Connector connector;
    private final BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateRelay;
    final AtomicBoolean isConnected = new AtomicBoolean(false);

    @Inject
    RxBleDeviceImpl(
            BluetoothDevice bluetoothDevice,
            Connector connector,
            BehaviorRelay<RxBleConnection.RxBleConnectionState> connectionStateRelay
    ) {
        this.bluetoothDevice = bluetoothDevice;
        this.connector = connector;
        this.connectionStateRelay = connectionStateRelay;
    }

    @Override
    public Observable<RxBleConnection.RxBleConnectionState> observeConnectionStateChanges() {
        return connectionStateRelay.distinctUntilChanged().skip(1);
    }

    @Override
    public RxBleConnection.RxBleConnectionState getConnectionState() {
        return connectionStateRelay.getValue();
    }

    @Override
    public Observable<RxBleConnection> establishConnection(final boolean autoConnect) {
        ConnectionSetup options = new ConnectionSetup.Builder()
                .setAutoConnect(autoConnect)
                .setSuppressIllegalOperationCheck(true)
                .build();
        return establishConnection(options);
    }

    @Override
    public Observable<RxBleConnection> establishConnection(final boolean autoConnect, final Timeout timeout) {
        ConnectionSetup options = new ConnectionSetup.Builder()
                .setAutoConnect(autoConnect)
                .setOperationTimeout(timeout)
                .setSuppressIllegalOperationCheck(true)
                .build();
        return establishConnection(options);
    }

    public Observable<RxBleConnection> establishConnection(final ConnectionSetup options) {
        return Observable.defer(new Callable<ObservableSource<RxBleConnection>>() {
            @Override
            public ObservableSource<RxBleConnection> call() {
                if (isConnected.compareAndSet(false, true)) {
                    return connector.prepareConnection(options)
                            .doFinally(new Action() {
                                @Override
                                public void run() {
                                    isConnected.set(false);
                                }
                            });
                } else {
                    return Observable.error(new BleAlreadyConnectedException(bluetoothDevice.getAddress()));
                }
            }
        });
    }

    @Override
    @Nullable
    public String getName() {
        return bluetoothDevice.getName();
    }

    @Override
    public String getMacAddress() {
        return bluetoothDevice.getAddress();
    }

    @Override
    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RxBleDeviceImpl)) {
            return false;
        }

        RxBleDeviceImpl that = (RxBleDeviceImpl) o;
        return bluetoothDevice.equals(that.bluetoothDevice);
    }

    @Override
    public int hashCode() {
        return bluetoothDevice.hashCode();
    }

    @Override
    public String toString() {
        return "RxBleDeviceImpl{"
                + LoggerUtil.commonMacMessage(bluetoothDevice.getAddress())
                + ", name=" + bluetoothDevice.getName()
                + '}';
    }
}
