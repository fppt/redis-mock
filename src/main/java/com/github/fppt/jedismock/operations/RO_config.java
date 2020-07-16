package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.storage.OperationExecutorState;
import com.google.common.collect.Lists;
import java.util.List;

public class RO_config implements RedisOperation {

    private final OperationExecutorState state;
    private final List<Slice> params;

    RO_config(OperationExecutorState state, List<Slice> params) {
        this.state = state;
        this.params = params;
    }

    @Override
    public Slice execute() {
        int parameterCount = params.size();
        if (parameterCount == 2) {
            return doGet();
        } else if (parameterCount == 3) {
            return doSet();
        }
        throw new IllegalArgumentException("Invalid number of arguments for config command, got " + parameterCount + " expected 2 or 3.");
    }

    private Slice doGet() {
        Slice configKey = params.get(1);
        return Response.array(Lists.newArrayList(Response.bulkString(configKey), Response.bulkString(state.base().getConfig(configKey))));
    }

    private Slice doSet() {
        Slice configKey = params.get(1);
        Slice configValue = params.get(2);
        state.base().setConfig(configKey, configValue);
        return Response.OK;
    }
}
