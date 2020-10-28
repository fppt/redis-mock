package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.OperationExecutorState;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class RO_exec implements RedisOperation {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RO_exec.class);
    private OperationExecutorState state;

    public RO_exec(OperationExecutorState state) {
        this.state = state;
    }

    @Override
    public Slice execute() {
        try {
            state.transactionMode(false);
            List<Slice> results = state.tx().stream().
                    map(RedisOperation::execute).
                    collect(Collectors.toList());
            state.tx().clear();
            return Response.array(results);
        } catch (Throwable t){
            LOG.error("ERROR during committing transaction", t);
            return Response.NULL;
        }
    }
}
