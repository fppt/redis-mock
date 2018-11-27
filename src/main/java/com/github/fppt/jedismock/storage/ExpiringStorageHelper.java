package com.github.fppt.jedismock.storage;

import com.github.fppt.jedismock.Slice;

import java.util.function.Supplier;

public final class ExpiringStorageHelper {

    public static Slice get(Long deadline, Supplier<Slice> getter, Runnable cacheCleaner){
        if (deadline != null && deadline != -1 && deadline <= System.currentTimeMillis()) {
            cacheCleaner.run();
            return null;
        }
        return getter.get();
    }

    public static Long getTTL(Long deadline, Runnable cacheCleaner){
        if (deadline == null) {
            return null;
        }
        if (deadline == -1) {
            return deadline;
        }
        long now = System.currentTimeMillis();
        if (now < deadline) {
            return deadline - now;
        }
        cacheCleaner.run();
        return null;
    }
}
