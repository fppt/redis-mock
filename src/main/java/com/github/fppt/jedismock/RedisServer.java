package com.github.fppt.jedismock;

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.net.ServerSocket;

/**
 * Created by Xiaolu on 2015/4/18.
 */
public class RedisServer {

    private final int bindPort;
    private ServiceOptions options = ServiceOptions.defaultOptions();
    private ServerSocket server = null;
    private Thread service = null;
    private final RedisBase base = new RedisBase();

    public RedisServer() throws IOException {
        this(0);
    }

    public RedisServer(int port) throws IOException {
        this.bindPort = port;
    }

    static public RedisServer newRedisServer() throws IOException {
        return new RedisServer();
    }

    static public RedisServer newRedisServer(int port) throws IOException {
        return new RedisServer(port);
    }

    public void setOptions(ServiceOptions options) {
        Preconditions.checkNotNull(options);

        this.options = options;
    }

    public void start() throws IOException {
        Preconditions.checkState(server == null);
        Preconditions.checkState(service == null);

        server = new ServerSocket(bindPort);
        service = new Thread(new RedisService(server, base, options));
        service.start();
    }

    public void stop() {
        Preconditions.checkNotNull(service);
        Preconditions.checkState(service.isAlive());


        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
            // do nothing
        }

        try {
            service.join(100);
            if (service.isAlive()) {
                service.stop();
            }
        } catch (InterruptedException e) {
            service.stop();
        }

        server = null;
        service = null;
    }

    public String getHost() {
        Preconditions.checkNotNull(server);

        return server.getInetAddress().getHostAddress();
    }

    public int getBindPort() {
        Preconditions.checkNotNull(server);

        return server.getLocalPort();
    }

    public RedisBase getBase() {
        return base;
    }

    public void setSlave(RedisServer slave) {
        Preconditions.checkState(server == null);
        Preconditions.checkState(service == null);

        base.addSyncBase(slave.getBase());
    }
}
