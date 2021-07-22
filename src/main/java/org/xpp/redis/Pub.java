package org.xpp.redis;

import redis.clients.jedis.Jedis;

public class Pub {
    public static void main(String[] args) {
        System.out.println("消息发布端");
        Jedis jedis = new Jedis("linux1", 6379);
        jedis.publish("channel1", "message1");
        // 模拟取消订阅
        jedis.publish("channel1", "unsubscribe");
        jedis.publish("pattern0", "pattern0");
        jedis.disconnect();
    }
}
