package com.example.essentialstracker.internal.connection;

import androidx.annotation.RestrictTo;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface PayloadSizeLimitProvider {

    int getPayloadSizeLimit();
}
