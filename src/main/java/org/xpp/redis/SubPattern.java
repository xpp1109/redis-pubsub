package org.xpp.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

public class SubPattern {
    public static void main(String[] args) {
        System.out.println("消息订阅端-订阅模式");
        Jedis jedis = new Jedis("linux1", 6379);
        JedisPubSub jedisPubSub = new PubSubListener();
        jedis.psubscribe(jedisPubSub, "pattern*");
        jedis.disconnect();
    }
}
