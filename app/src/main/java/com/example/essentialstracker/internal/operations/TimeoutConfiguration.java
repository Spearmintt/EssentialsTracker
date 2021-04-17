package com.example.essentialstracker.internal.operations;

import java.util.concurrent.TimeUnit;

import io.reactivex.Scheduler;


public class TimeoutConfiguration {

    public final long timeout;
    public final TimeUnit timeoutTimeUnit;
    public final Scheduler timeoutScheduler;

    public TimeoutConfiguration(long timeout, TimeUnit timeoutTimeUnit, Scheduler timeoutScheduler) {
        this.timeout = timeout;
        this.timeoutTimeUnit = timeoutTimeUnit;
        this.timeoutScheduler = timeoutScheduler;
    }

    @Override
    public String toString() {
        return "{value=" + timeout
                + ", timeUnit=" + timeoutTimeUnit
                + '}';
    }
}
