package com.github.fppt.jedismock;

import com.google.auto.value.AutoValue;

/**
 * Created by Xiaolu on 2015/4/22.
 */
@AutoValue
public abstract class ServiceOptions {
    public abstract int autoCloseOn();

    public static ServiceOptions defaultOptions() {
        return new AutoValue_ServiceOptions(0);
    }

    public static ServiceOptions create(int autoCloseOn) {
        return new AutoValue_ServiceOptions(autoCloseOn);
    }
}
