package com.github.lemonj.jedismock.operations;

import com.github.lemonj.jedismock.Utils;
import com.github.lemonj.jedismock.operations.script.RedisBinding;
import com.github.lemonj.jedismock.operations.script.ThreadLocalUtil;
import com.github.lemonj.jedismock.server.Response;
import com.github.lemonj.jedismock.server.Slice;
import com.github.lemonj.jedismock.storage.RedisBase;
import com.google.common.collect.Lists;
import org.luaj.vm2.LuaDouble;
import org.luaj.vm2.LuaInteger;
import org.luaj.vm2.LuaNumber;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.List;

/**
 * @Author: lmj
 * @Description:
 * @Date: Create in 10:30 上午 2020/12/29
 **/
class RO_eval extends AbstractRedisOperation {

    RO_eval(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    @Override
    Slice response() {
        ScriptCommand scriptCommand = ScriptCommand.getInstance(params());
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("luaj");
        engine.put("redis", this.createBinding(new RedisBinding(ThreadLocalUtil.getExecutor())));
        engine.put("KEYS", scriptCommand.getKeys());
        engine.put("ARGV", scriptCommand.getArgv());
        try {
            return Response.bulkString(convert(engine.eval(scriptCommand.getCommand())));
        } catch (ScriptException e) {
            //not support
            return Response.error(e.getMessage());
        }
    }

    /**
     * scriptCommand
     */
    private static class ScriptCommand {
        private String command;

        private String[] keys;

        private String[] argv;


        public String getCommand() {
            return command;
        }

        public void setCommand(String command) {
            this.command = command;
        }

        public String[] getKeys() {
            return keys;
        }

        public void setKeys(String[] keys) {
            this.keys = keys;
        }

        public String[] getArgv() {
            return argv;
        }

        public void setArgv(String[] argv) {
            this.argv = argv;
        }

        public static ScriptCommand getInstance(List<Slice> params) {
            ScriptCommand scriptCommand = new ScriptCommand();
            scriptCommand.setCommand(params.get(0).toString());

            long keyCount = Utils.convertToLong(String.valueOf(params.get(1)));
            scriptCommand.setKeys(params.stream().skip(2).limit(keyCount).map(Slice::toString).toArray(String[]::new));
            scriptCommand.setArgv(params.stream().skip(2 + keyCount).map(Slice::toString).toArray(String[]::new));

            return scriptCommand;
        }
    }

    private LuaValue createBinding(RedisBinding redis) {
        LuaTable binding = LuaTable.tableOf();
        binding.set("call", redis);
        return binding;
    }

    private Slice convert(Object source) {
        if (source instanceof LuaTable) {
            List<Slice> result = Lists.newArrayList();
            LuaTable value = (LuaTable) source;
            for (LuaValue key : value.keys()) {
                result.add(convert(value.get(key)));
            }
            return Response.array(result);
        } else if (source instanceof LuaInteger) {
            return Response.integer(((LuaInteger) source).toint());
        } else if (source instanceof LuaDouble) {
            return Response.doubleValue(((LuaDouble) source).toint());
        }

        return Slice.create(String.valueOf(source));
    }
}
