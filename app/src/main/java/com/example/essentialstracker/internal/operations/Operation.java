package com.example.essentialstracker.internal.operations;

import androidx.annotation.RestrictTo;

import com.example.essentialstracker.internal.Priority;
import com.example.essentialstracker.internal.serialization.QueueReleaseInterface;

import io.reactivex.Observable;

@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public interface Operation<T> extends Comparable<Operation<?>> {

    Observable<T> run(QueueReleaseInterface queueReleaseInterface);

    Priority definedPriority();
}
