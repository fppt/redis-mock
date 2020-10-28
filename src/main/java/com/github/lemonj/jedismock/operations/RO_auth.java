package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.OperationExecutorState;

public class RO_auth implements RedisOperation {
    private OperationExecutorState state;

    public RO_auth(OperationExecutorState state) {
        this.state = state;
    }

    @Override
    public Slice execute() {
        state.owner().sendResponse(Response.clientResponse("auth", Response.OK), "auth");
        return Response.SKIP;
    }
}
