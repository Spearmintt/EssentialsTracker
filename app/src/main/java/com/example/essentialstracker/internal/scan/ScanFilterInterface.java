package com.example.essentialstracker.internal.scan;

import com.example.essentialstracker.internal.ScanResultInterface;

public interface ScanFilterInterface {

    boolean isAllFieldsEmpty();

    boolean matches(ScanResultInterface scanResult);
}
