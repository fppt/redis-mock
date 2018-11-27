package com.github.fppt.jedismock;

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
    static RedisCommand parse(String stringInput) throws ParseErrorException {
        Preconditions.checkNotNull(stringInput);

        return parse(new ByteArrayInputStream(stringInput.getBytes()));
    }

    static RedisCommand parse(InputStream messageInput) throws ParseErrorException {
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
