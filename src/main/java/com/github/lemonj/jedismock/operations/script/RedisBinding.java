package com.github.lemonj.jedismock.operations.script;

import com.github.lemonj.jedismock.commands.RedisCommand;
import com.github.lemonj.jedismock.server.RedisOperationExecutor;
import com.github.lemonj.jedismock.server.Slice;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.VarArgFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * create by lmj for execute lua binding
 **/
public class RedisBinding extends VarArgFunction {

    private RedisOperationExecutor executor;

    public RedisBinding(RedisOperationExecutor executor) {
        this.executor = executor;
    }

    @Override
    public Varargs invoke(Varargs args) {
        RedisCommand redisCommand = RedisCommand.create();
        redisCommand.parameters().add(Slice.create(readCommand(args)));
        redisCommand.parameters().addAll(readArguments(args).stream()
                .map(Slice::create)
                .collect(Collectors.toList()));
        String result = String.valueOf(executor.execCommand(redisCommand));

        return toLuaString(result);
    }


    private List<byte[]> readArguments(Varargs args) {
        List<byte[]> params = new ArrayList();
        if (args.narg() > 1) {
            for (int i = 1; i < args.narg(); ++i) {
                params.add(this.toSafeString(args.checkstring(i + 1)));
            }
        }

        return params;
    }

    private byte[] readCommand(Varargs args) {
        return this.toSafeString(args.checkstring(1));
    }

    private byte[] toSafeString(LuaString value) {
        return new SafeString(value.m_bytes).getBytes();
    }

    private LuaValue toLuaString(String value) {
        return (value == null ? LuaValue.NIL : LuaString.valueOf(value.getBytes()));
    }
}
