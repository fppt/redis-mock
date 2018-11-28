package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.RedisBase;
import com.github.fppt.jedismock.RedisClient;
import com.github.fppt.jedismock.RedisCommand;
import com.github.fppt.jedismock.Response;
import com.github.fppt.jedismock.Slice;
import com.github.fppt.jedismock.exception.WrongValueTypeException;
import com.google.common.base.Preconditions;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

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

    private RedisOperation buildSimpleOperation(String name, List<Slice> params){
        switch(name){
            case "set":
                return new RO_set(base, params);
            case "setex":
                return new RO_setex(base, params);
            case "psetex":
                return new RO_psetex(base, params);
            case "setnx":
                return new RO_setnx(base, params);
            case "setbit":
                return new RO_setbit(base, params);
            case "append":
                return new RO_append(base, params);
            case "get":
                return new RO_get(base, params);
            case "getbit":
                return new RO_getbit(base, params);
            case "ttl":
                return new RO_ttl(base, params);
            case "pttl":
                return new RO_pttl(base, params);
            case "expire":
                return new RO_expire(base, params);
            case "pexpire":
                return new RO_pexpire(base, params);
            case "incr":
                return new RO_incr(base, params);
            case "incrby":
                return new RO_incrby(base, params);
            case "decr":
                return new RO_decr(base, params);
            case "decrby":
                return new RO_decrby(base, params);
            case "pfcount":
                return new RO_pfcount(base, params);
            case "pfadd":
                return new RO_pfadd(base, params);
            case "pfmerge":
                return new RO_pfmerge(base, params);
            case "mget":
                return new RO_mget(base, params);
            case "mset":
                return new RO_mset(base, params);
            case "getset":
                return new RO_getset(base, params);
            case "strlen":
                return new RO_strlen(base, params);
            case "del":
                return new RO_del(base, params);
            case "exists":
                return new RO_exists(base, params);
            case "expireat":
                return new RO_expireat(base, params);
            case "pexpireat":
                return new RO_pexpireat(base, params);
            case "lpush":
                return new RO_lpush(base, params);
            case "rpush":
                return new RO_rpush(base, params);
            case "lpushx":
                return new RO_lpushx(base, params);
            case "lrange":
                return new RO_lrange(base, params);
            case "llen":
                return new RO_llen(base, params);
            case "lpop":
                return new RO_lpop(base, params);
            case "rpop":
                return new RO_rpop(base, params);
            case "lindex":
                return new RO_lindex(base, params);
            case "rpoplpush":
                return new RO_rpoplpush(base, params);
            case "brpoplpush":
                return new RO_brpoplpush(base, params);
            case "subscribe":
                return new RO_subscribe(base, owner, params);
            case "unsubscribe":
                return new RO_unsubscribe(base, owner, params);
            case "publish":
                return new RO_publish(base, params);
            case "flushall":
                return new RO_flushall(base, params);
            case "lrem":
                return new RO_lrem(base, params);
            case "quit":
                return new RO_quit(base, owner, params);
            case "exec":
                transactionModeOn = false;
                return new RO_exec(base, transaction, params);
            case "ping":
                return new RO_ping(base, params);
            case "keys":
                return new RO_keys(base, params);
            case "sadd":
                return new RO_sadd(base, params);
            case "smembers":
                return new RO_smembers(base, params);
            case "spop":
                return new RO_spop(base, params);
            case "hget":
                return new RO_hget(base, params);
            case "hset":
                return new RO_hset(base, params);
            case "hdel":
                return new RO_hdel(base, params);
            default:
                throw new UnsupportedOperationException(String.format("Unsupported operation '%s'", name));
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

            //Checking if we mutating the transaction or the base
            RedisOperation redisOperation = buildSimpleOperation(name, commandParams);
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
