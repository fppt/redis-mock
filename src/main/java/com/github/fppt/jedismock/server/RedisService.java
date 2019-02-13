package com.github.fppt.jedismock.server;

import com.github.fppt.jedismock.storage.RedisBase;
import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

/**
 * Created by Xiaolu on 2015/4/21.
 */
public class RedisService implements Runnable {

    private final ServerSocket server;
    private final Map<Integer, RedisBase> redisBases;
    private final ServiceOptions options;

    public RedisService(ServerSocket server, Map<Integer, RedisBase> redisBases, ServiceOptions options) {
        Preconditions.checkNotNull(server);
        Preconditions.checkNotNull(redisBases);
        Preconditions.checkNotNull(options);

        this.server = server;
        this.redisBases = redisBases;
        this.options = options;
    }

    public void run() {
        while (!server.isClosed()) {
            try {
                Socket socket = server.accept();
                Thread t = new Thread(new RedisClient(redisBases, socket, options));
                t.start();
            } catch (IOException e) {
                // Do noting
            }
        }
    }
}
