package com.example.essentialstracker.internal.scan;

import com.example.essentialstracker.exceptions.BleScanException;

public interface ScanPreconditionsVerifier {

    void verify(boolean checkLocationProviderState) throws BleScanException;
}
