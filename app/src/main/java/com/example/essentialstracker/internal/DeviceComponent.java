package com.example.essentialstracker.internal;

import com.example.essentialstracker.RxBleDevice;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Subcomponent;

import static com.example.essentialstracker.internal.DeviceModule.MAC_ADDRESS;

@DeviceScope
@Subcomponent(modules = {DeviceModule.class})
public interface DeviceComponent {

    @Subcomponent.Builder
    interface Builder {
        DeviceComponent build();

        @BindsInstance
        Builder macAddress(@Named(MAC_ADDRESS) String deviceMacAddress);
    }

    @DeviceScope
    RxBleDevice provideDevice();
}
