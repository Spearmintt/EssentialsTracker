package com.example.essentialstracker.internal.connection;

import com.example.essentialstracker.exceptions.BleException;

import io.reactivex.Observable;

/**
 * Interface to output disconnection error causes. It is used for instance to notify when the
 * {@link com.example.essentialstracker.internal.serialization.ConnectionOperationQueue} should terminate because of a connection error
 */
public interface DisconnectionRouterOutput {

    /**
     * Function returning an Observable that will only emit value in case of a disconnection (will never emit an error)
     *
     * @return the Observable
     */
    Observable<BleException> asValueOnlyObservable();

    /**
     * Function returning an Observable that will only throw error in case of a disconnection (will never emit value)
     *
     * @param <T> the type of returned observable
     * @return the Observable
     */
    <T> Observable<T> asErrorOnlyObservable();
}

