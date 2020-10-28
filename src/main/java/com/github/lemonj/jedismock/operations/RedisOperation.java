package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.storage.RedisBase;
import com.github.lemonj.jedismock.server.Slice;

/**
 * Represents a Redis Operation which can be executed against {@link RedisBase}
 */
public interface RedisOperation {
    Slice execute();
}
