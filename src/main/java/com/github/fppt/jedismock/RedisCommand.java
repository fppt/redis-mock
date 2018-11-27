package com.github.fppt.jedismock;

import com.google.auto.value.AutoValue;
import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

@AutoValue
public abstract class RedisCommand {

    public abstract List<Slice> parameters();

    static RedisCommand create(){
        return new AutoValue_RedisCommand(Lists.newArrayList());
    }

    @Override
    public String toString(){
        return parameters().stream().map(Slice::toString).collect(Collectors.joining(" "));
    }
}
