package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.RedisClient;
import com.github.fppt.jedismock.RedisCommand;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.github.fppt.jedismock.exception.WrongValueTypeException;
import com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class RedisOperationExecutor {
    private static final org.slf4j.Logger LOG = LoggerFactory.getLogger(RedisOperationExecutor.class);
    private final RedisClient owner;
    private final RedisBase base;
    private boolean transactionModeOn;
    private List<RedisOperation> transaction;

    public RedisOperationExecutor(RedisBase base, RedisClient owner) {
        this.base = base;
        this.owner = owner;
        transactionModeOn = false;
        transaction = new ArrayList<>();
    }

    private RedisOperation buildRedisOperation(String name, List<Slice> params){
        try {
            String className = this.getClass().getPackage().getName() + ".RO_" + name;
            Class<?> clazz = Class.forName(className);
            @SuppressWarnings("unchecked")
            Constructor<RedisOperation> constructor = (Constructor<RedisOperation>) clazz.getDeclaredConstructors()[0];
            return constructor.newInstance(base, params);
        } catch (ClassNotFoundException e) {
            throw new UnsupportedOperationException(String.format("Unsupported operation '%s'", name));
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | IllegalArgumentException e) {
            throw new UnsupportedOperationException(String.format("Error initialising operation '%s'", name));
        }
    }

    private Optional<RedisOperation> buildMetaOperation(String name, List<Slice> params){
        switch(name){
            case "quit":
                return Optional.of(new RO_quit(base, owner, params));
            case "exec":
                transactionModeOn = false;
                return Optional.of(new RO_exec(base, transaction, params));
            case "subscribe":
                return Optional.of(new RO_subscribe(base, owner, params));
            case "unsubscribe":
                return Optional.of(new RO_unsubscribe(base, owner, params));
            default:
                return Optional.empty();
        }
    }

    public synchronized Slice execCommand(RedisCommand command) {
        Preconditions.checkArgument(command.parameters().size() > 0);
        List<Slice> params = command.parameters();
        List<Slice> commandParams = params.subList(1, params.size());
        String name = new String(params.get(0).data()).toLowerCase();

        try {
            //Transaction handling
            if(name.equals("multi")){
                newTransaction();
                return Response.clientResponse(name, Response.OK);
            }

            RedisOperation redisOperation = buildMetaOperation(name, commandParams).
                    orElseGet(() -> buildRedisOperation(name, commandParams));

            //Checking if we mutating the transaction or the base
            if(transactionModeOn){
                transaction.add(redisOperation);
            } else {
                return Response.clientResponse(name, redisOperation.execute());
            }

            return Response.clientResponse(name, Response.OK);
        } catch(UnsupportedOperationException | WrongValueTypeException | IllegalArgumentException e){
            LOG.error("Malformed request", e);
            return Response.error(e.getMessage());
        }
    }

    private void newTransaction(){
        if(transactionModeOn) throw new RuntimeException("Redis mock does not support more than one transaction");
        transactionModeOn = true;
    }
}
