package org.xpp.redis;

import redis.clients.jedis.JedisPubSub;

public class PubSubListener extends JedisPubSub {
    public PubSubListener() {
        super();
    }

    /**
     * 订阅频道接收到消息回调
     *
     * @param channel
     * @param message
     */
    @Override
    public void onMessage(String channel, String message) {
        super.onMessage(channel, message);
        System.out.println("订阅频道接收到消息回调, channel=" + channel + "，message=" + message);
        if ("unsubscribe".equals(message)) {
            this.unsubscribe();
        }
    }

    /**
     * 订阅模式接收到消息回调
     *
     * @param pattern
     * @param channel
     * @param message
     */
    @Override
    public void onPMessage(String pattern, String channel, String message) {
        super.onPMessage(pattern, channel, message);
        System.out.println("订阅模式接收到消息回调, pattern=" + pattern + ", channel=" + channel + "，message=" + message);
    }

    /**
     * 订阅频道回调
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onSubscribe(String channel, int subscribedChannels) {
        super.onSubscribe(channel, subscribedChannels);
        System.out.println("订阅频道回调, channel=" + channel + "，subscribedChannels=" + subscribedChannels);
    }

    /**
     * 取消订阅通道回调
     * @param channel
     * @param subscribedChannels
     */
    @Override
    public void onUnsubscribe(String channel, int subscribedChannels) {
        super.onUnsubscribe(channel, subscribedChannels);
        System.out.println("取消订阅频道回调, channel=" + channel + "，subscribedChannels=" + subscribedChannels);

    }

    /**
     * 取消订阅模式回调
     * @param pattern
     * @param subscribedChannels
     */
    @Override
    public void onPUnsubscribe(String pattern, int subscribedChannels) {
        super.onPUnsubscribe(pattern, subscribedChannels);
        System.out.println("取消订阅模式回调, pattern=" + pattern + "，subscribedChannels=" + subscribedChannels);
    }

    /**
     * 订阅模式回调
     */
    @Override
    public void onPSubscribe(String pattern, int subscribedChannels) {
        super.onPSubscribe(pattern, subscribedChannels);
        System.out.println("订阅模式回调, pattern=" + pattern + "，subscribedChannels=" + subscribedChannels);
    }
}
