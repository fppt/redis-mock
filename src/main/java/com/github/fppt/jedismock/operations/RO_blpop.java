package com.github.fppt.jedismock.operations;

import com.github.fppt.jedismock.server.Response;
import com.github.fppt.jedismock.server.Slice;
import com.github.fppt.jedismock.server.SliceParser;
import com.github.fppt.jedismock.storage.RedisBase;
import com.google.common.collect.ImmutableList;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static com.github.fppt.jedismock.Utils.serializeObject;

import static com.github.fppt.jedismock.Utils.convertToLong;

class RO_blpop extends RO_lpop {
    private long count = 0L;
    private Slice source;

    RO_blpop(RedisBase base, List<Slice> params) {
        super(base, params);
    }

    void doOptionalWork() {
        int size = params().size();
        long timeout = convertToLong(params().get(size - 1).toString());
        List<Slice> lists = params().subList(0, size - 2);
        //TODO: Remove active block dumb.
        long currentSleep = 0L;
        while (count == 0L && currentSleep < timeout * 1000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            currentSleep = currentSleep + 100;
            updateCountAndSource(lists);
        }
    }

    Slice response() {
        if (count != 0L) {
            LinkedList<Slice> list = getDataFromBase(source, null);
            if (list == null || list.isEmpty()) return Response.NULL;
            Slice v = popper(list);
            base().putValue(source, serializeObject(list));
            ImmutableList.Builder<Slice> builder = new ImmutableList.Builder<>();
            builder.add(Response.bulkString(source));
            builder.add(Response.bulkString(v));
            this.source = null;
            this.count = 0L;
            return Response.array(builder.build());
        } else {
            return Response.NULL;
        }
    }

    private void updateCountAndSource(List<Slice> list) {
        this.source = null;
        this.count = 0L;
        for (Slice source : list) {
            Slice index = Slice.create("0");
            List<Slice> commands = Arrays.asList(source, index, index);
            Slice result = new RO_lrange(base(), commands).execute();
            long count = SliceParser.consumeCount(result.data());
            if (count > 0L) {
                this.source = source;
                this.count = count;
                return;
            }
        }
    }

}
