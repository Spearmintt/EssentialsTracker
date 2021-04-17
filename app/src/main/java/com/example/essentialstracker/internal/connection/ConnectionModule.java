package com.example.essentialstracker.internal.connection;

import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;

import com.example.essentialstracker.ClientComponent;
import com.example.essentialstracker.RxBleConnection;
import com.example.essentialstracker.Timeout;
import com.example.essentialstracker.internal.operations.OperationsProvider;
import com.example.essentialstracker.internal.operations.OperationsProviderImpl;
import com.example.essentialstracker.internal.operations.TimeoutConfiguration;
import com.example.essentialstracker.internal.serialization.ConnectionOperationQueue;
import com.example.essentialstracker.internal.serialization.ConnectionOperationQueueImpl;
import com.example.essentialstracker.internal.util.CharacteristicPropertiesParser;

import javax.inject.Named;
import javax.inject.Provider;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import io.reactivex.Scheduler;

import static com.example.essentialstracker.internal.connection.ConnectionComponent.NamedBooleans.SUPPRESS_OPERATION_CHECKS;
import static com.example.essentialstracker.internal.connection.ConnectionComponent.NamedInts.GATT_MTU_MINIMUM;
import static com.example.essentialstracker.internal.connection.ConnectionComponent.NamedInts.GATT_WRITE_MTU_OVERHEAD;

@Module
public abstract class ConnectionModule {

    public static final String OPERATION_TIMEOUT = "operation-timeout";

    @Provides
    @Named(OPERATION_TIMEOUT)
    static TimeoutConfiguration providesOperationTimeoutConf(
            @Named(ClientComponent.NamedSchedulers.TIMEOUT) Scheduler timeoutScheduler,
            Timeout operationTimeout
    ) {
        return new TimeoutConfiguration(operationTimeout.timeout, operationTimeout.timeUnit, timeoutScheduler);
    }

    @Provides
    static IllegalOperationHandler provideIllegalOperationHandler(
            @Named(SUPPRESS_OPERATION_CHECKS) boolean suppressOperationCheck,
            Provider<LoggingIllegalOperationHandler> loggingIllegalOperationHandlerProvider,
            Provider<ThrowingIllegalOperationHandler> throwingIllegalOperationHandlerProvider
    ) {
        if (suppressOperationCheck) {
            return loggingIllegalOperationHandlerProvider.get();
        } else {
            return throwingIllegalOperationHandlerProvider.get();
        }
    }

    @Provides
    static CharacteristicPropertiesParser provideCharacteristicPropertiesParser() {
        return new CharacteristicPropertiesParser(BluetoothGattCharacteristic.PROPERTY_BROADCAST,
                BluetoothGattCharacteristic.PROPERTY_READ,
                BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE,
                BluetoothGattCharacteristic.PROPERTY_WRITE,
                BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PROPERTY_INDICATE,
                BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE);
    }

    @Provides
    @Named(GATT_WRITE_MTU_OVERHEAD)
    static int gattWriteMtuOverhead() {
        return RxBleConnection.GATT_WRITE_MTU_OVERHEAD;
    }

    @Provides
    @Named(GATT_MTU_MINIMUM)
    static int minimumMtu() {
        return RxBleConnection.GATT_MTU_MINIMUM;
    }

    @Provides
    static BluetoothGatt provideBluetoothGatt(BluetoothGattProvider bluetoothGattProvider) {
        return bluetoothGattProvider.getBluetoothGatt();
    }

    @Binds
    abstract RxBleConnection.LongWriteOperationBuilder bindLongWriteOperationBuilder(LongWriteOperationBuilderImpl operationBuilder);

    @Binds
    abstract OperationsProvider bindOperationsProvider(OperationsProviderImpl operationsProvider);

    @Binds
    abstract MtuProvider bindCurrentMtuProvider(MtuWatcher mtuWatcher);

    @Binds
    @IntoSet
    abstract ConnectionSubscriptionWatcher bindMtuWatcherSubscriptionWatcher(MtuWatcher mtuWatcher);

    @Binds
    @IntoSet
    abstract ConnectionSubscriptionWatcher bindDisconnectActionSubscriptionWatcher(DisconnectAction disconnectAction);

    @Binds
    @IntoSet
    abstract ConnectionSubscriptionWatcher bindConnectionQueueSubscriptionWatcher(ConnectionOperationQueueImpl connectionOperationQueue);

    @Binds
    abstract RxBleConnection bindRxBleConnection(RxBleConnectionImpl rxBleConnection);

    @Binds
    abstract ConnectionOperationQueue bindConnectionOperationQueue(ConnectionOperationQueueImpl connectionOperationQueue);

    @Binds
    abstract DisconnectionRouterInput bindDisconnectionRouterInput(DisconnectionRouter disconnectionRouter);

    @Binds
    abstract DisconnectionRouterOutput bindDisconnectionRouterOutput(DisconnectionRouter disconnectionRouter);
}