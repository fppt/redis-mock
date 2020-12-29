package com.github.lemonj.jedismock.operations.script;

import java.util.HashMap;
import java.util.Map;

/**
 * create by lmj
 **/
public class ThreadLocalUtil {
    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(() -> new HashMap(4));
    private static final String EXECUTOR = "EXECUTOR";

    public static <T> T getExecutor() {
        Map map = threadLocal.get();
        return (T) map.get(EXECUTOR);
    }

    public static void setExecutor(Object executor) {
        threadLocal.get().put(EXECUTOR, executor);
    }
}