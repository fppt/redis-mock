package com.github.lemonj.jedismock.operations.script;

import com.github.lemonj.jedismock.commands.RedisCommand;
import com.github.lemonj.jedismock.server.RedisOperationExecutor;
import com.github.lemonj.jedismock.server.Slice;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaString;
import org.luaj.vm2.LuaTable;
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
        Slice result = executor.execCommand(redisCommand);

        return toLuaValue(result);
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


    /**
     * single paramCount
     *
     * @param value
     * @return
     */
    private LuaValue toLuaValue(Slice value) {
        if (value == null) {
            return LuaValue.NIL;
        }

        if (value.toString().startsWith("*")) {
            LuaTable table = LuaTable.tableOf();
            int iter = 0;
            int count = 0;
            String[] split = value.toString().split("\r\n");
            for (int i = 0; i < split.length; i++) {
                if (i == 0) {
                    continue;
                }
                if (count > 0) {
                    table.set(iter, toLuaValue(Slice.create(split[i])));
                    iter++;
                    count--;
                }
                if (split[i].startsWith("$")) {
                    if (table.length() <= 0) {
                        iter++;
                    }

                    String numString = split[i].substring(1);
                    if (numString.startsWith("-")) {
                        table.set(iter, LuaValue.NIL);
                        continue;
                    }

                    count = Integer.parseInt(numString);
                }
            }
            return table;
        }

        return toLuaString(value.toString());
    }

    private LuaValue toLuaString(String value) {
        if (value == null) {
            return LuaValue.NIL;
        }

        try {
            return LuaInteger.valueOf(Integer.parseInt(value));
        } catch (NumberFormatException n) {
        }

        return LuaString.valueOf(value.getBytes());
    }
}
