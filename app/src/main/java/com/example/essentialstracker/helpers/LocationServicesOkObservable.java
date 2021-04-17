package com.example.essentialstracker.helpers;

import android.content.Context;
import androidx.annotation.NonNull;

import com.example.essentialstracker.ClientComponent;
import com.example.essentialstracker.DaggerClientComponent;

import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Named;
import io.reactivex.Observable;
import io.reactivex.Observer;

/**
 * An Observable that emits false if an attempt to scan with {@link com.example.essentialstracker.RxBleClient#scanBleDevices(UUID...)}
 * would cause the exception {@link com.example.essentialstracker.exceptions.BleScanException#LOCATION_SERVICES_DISABLED}; otherwise emits true.
 * Always emits true in Android versions prior to 6.0.
 * Typically, receiving false should cause the user to be prompted to enable Location Services.
 */
public class LocationServicesOkObservable extends Observable<Boolean> {

    @NonNull
    private final Observable<Boolean> locationServicesOkObsImpl;

    public static LocationServicesOkObservable createInstance(@NonNull final Context context) {
        return DaggerClientComponent
                .builder()
                .applicationContext(context.getApplicationContext())
                .build()
                .locationServicesOkObservable();
    }

    @Inject
    LocationServicesOkObservable(
            @NonNull
            @Named(ClientComponent.NamedBooleanObservables.LOCATION_SERVICES_OK) final Observable<Boolean> locationServicesOkObsImpl) {
        this.locationServicesOkObsImpl = locationServicesOkObsImpl;
    }

    @Override
    protected void subscribeActual(final Observer<? super Boolean> observer) {
        locationServicesOkObsImpl.subscribe(observer);
    }
}
