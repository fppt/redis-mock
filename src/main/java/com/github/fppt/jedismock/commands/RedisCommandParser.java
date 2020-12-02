package com.github.fppt.jedismock.commands;

import com.github.fppt.jedismock.exception.EOFException;
import com.github.fppt.jedismock.server.SliceParser;
import com.github.fppt.jedismock.exception.ParseErrorException;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Created by Xiaolu on 2015/4/20.
 */
public class RedisCommandParser {

    @VisibleForTesting
    public static RedisCommand parse(String stringInput) throws ParseErrorException {
        Preconditions.checkNotNull(stringInput);

        try {
            return parse(new ByteArrayInputStream(stringInput.getBytes()));
        } catch (EOFException e){
            // This is a stream over an in-memory byte array, so we shouldn't get this error
            throw new ParseErrorException();
        }
    }

    public static RedisCommand parse(InputStream messageInput) throws ParseErrorException, EOFException {
        Preconditions.checkNotNull(messageInput);

        long count = SliceParser.consumeCount(messageInput);
        if (count == 0) {
            throw new ParseErrorException();
        }
        RedisCommand command = RedisCommand.create();
        for (long i = 0; i < count; i++) {
            command.parameters().add(SliceParser.consumeParameter(messageInput));
        }
        return command;
    }
}
