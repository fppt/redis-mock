package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.commands.RedisCommand;
import com.github.lemonj.jedismock.operations.script.ThreadLocalUtil;
import com.github.lemonj.jedismock.server.RedisOperationExecutor;
import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: lmj
 * @Description:
 * @Date: Create in 10:30 上午 2020/12/29
 **/
class RO_evalsha extends AbstractRedisOperation {

    RO_evalsha(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        List<Slice> params = params();
        Slice script;

        //handle evalsha
        if ((script = base().getValue(params.get(0))) != null) {
            RedisOperationExecutor executor = ThreadLocalUtil.getExecutor();
            return executor.execCommand(getCommand(script, params));
        }

        return Response.OK;
    }

    private RedisCommand getCommand(Slice script, List<Slice> paramList) {
        RedisCommand redisCommand = RedisCommand.create();
        redisCommand.parameters().add(Slice.create("eval"));
        redisCommand.parameters().add(script);
        redisCommand.parameters().addAll(paramList.stream().skip(1)
                .collect(Collectors.toList()));

        return redisCommand;
    }
}
