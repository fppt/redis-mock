package com.github.fppt.jedismock.util;

import redis.clients.jedis.JedisPubSub;

public class MockSubscriber extends JedisPubSub {

    private String latestReceivedFromChannel;
    private String latestReceivedMessage;

    @Override
    public void onMessage(String channel, String message) {
        latestReceivedFromChannel = channel;
        latestReceivedMessage = message;
    }

    public String latestChannel(){
        return latestReceivedFromChannel;
    }

    public String latestMessage(){
        return latestReceivedMessage;
    }

}
