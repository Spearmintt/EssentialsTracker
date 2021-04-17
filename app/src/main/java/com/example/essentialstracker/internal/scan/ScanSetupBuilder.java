package com.example.essentialstracker.internal.scan;

import androidx.annotation.RestrictTo;
import com.example.essentialstracker.scan.ScanFilter;
import com.example.essentialstracker.scan.ScanSettings;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface ScanSetupBuilder {

    ScanSetup build(ScanSettings scanSettings, ScanFilter... scanFilters);
}
