> Redis可以作为消息队列，实现消息发布订阅功能

> Redis pub/sub 主要有如下几个命令

* PSUBSCRIBE // 订阅一个或多个符合给定模式的频道
* PUBLISH // 将信息发送到指定的频道
* PUBSUB // 查看订阅与发布系统状态
* PUNSUBSCRIBE // 退订所有给定模式的频道
* SUBSCRIBE // 订阅给定的一个或多个频道的信息
* UNSUBSCRIBE // 退订给定的频道

> 搭建集群

* 集群搭建参考：https://www.cnblogs.com/pipi1109/p/15030622.html

> 使用客户端连接无中心结构的集群, 使用如下指令连接集群

​	`./redis-cli ../redis.conf -c`

* 窗口1,执行subscribe命令 创建订阅频道channel1

  ![image-20210721204759760](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210721204759760.png)

* 窗口2 执行向channel1发送消息的指令

  ![image-20210721204854602](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210721204854602.png)

* 重新打开窗口1，查看是否收到消息

  ![image-20210721204924945](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210721204924945.png)

  看到message1已经接收到。

* 在窗口2执行 `pubsub channels`

  ![image-20210722141257225](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722141257225.png)

  我们能看到订阅的channel1.

* 取消订阅：

  我本来是想在窗口1进行unsubscribe操作。发现执行不了，因为subscribe之后窗口就被阻塞 无法继续执行其他指令，那我怎么执行取消订阅呢？懵逼！！！

  **通过查阅文档发现redis-cli确实无法unsubscribe，只有在使用Jedis Java客户端的时候才能unsubscribe 通道**

  此处只做一下unsubscribe指令执行的演示。

  在窗口2执行`unsubscribe channel1`

  ![image-20210722142149570](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722142149570.png)

* PSUBSCIRBE和PUNSUBSCRIBE 这两个指令类似SUBSCIRBE 和 UNSUBSCRIBE 不同的是前者支持通配符

  例如PSUBSCRIBE channel* 那么例如 channel1、channela、channelxxxxx等等都会被订阅。取消订阅亦如此

  再打开窗口3执行`PSUBSCRIBE CHANNEL*` 窗口2执行`PUBLISH CHANNEL1 "M1"`和`PUBLISH CHANNELX "M2"`

  ![窗口3](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722142605635.png)

  ![窗口2](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722142647822.png)

  回看窗口3：

  ![回看窗口3](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722142722528.png)

  收到了两个channel的消息。

> ## 代码示例（Java Jedis） Maven项目

* 引入pom依赖

  ```xml
  <?xml version="1.0" encoding="UTF-8"?>
  <project xmlns="http://maven.apache.org/POM/4.0.0"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
      <modelVersion>4.0.0</modelVersion>
  
      <groupId>org.xpp</groupId>
      <artifactId>redis-pubsub</artifactId>
      <version>1.0-SNAPSHOT</version>
  
      <properties>
          <maven.compiler.source>16</maven.compiler.source>
          <maven.compiler.target>16</maven.compiler.target>
      </properties>
      <dependencies>
          <dependency>
              <groupId>redis.clients</groupId>
              <artifactId>jedis</artifactId>
              <version>3.6.3</version>
          </dependency>
      </dependencies>
  </project>
  ```



* 创建订阅处理监听类PubSubListener

  ```java
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
          System.out.println("订阅频道接收到消息回调, pattern=" + pattern + ", channel=" + channel + "，message=" + message);
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
  ```



* 创建订阅类 Sub

  ```java
  package org.xpp.redis;
  
  import redis.clients.jedis.Jedis;
  import redis.clients.jedis.JedisPubSub;
  
  public class Sub {
      public static void main(String[] args) {
          System.out.println("消息订阅端-订阅channel");
          Jedis jedis = new Jedis("linux1", 6379);
          JedisPubSub jedisPubSub = new PubSubListener();
          jedis.subscribe(jedisPubSub, "channel1");
          jedis.disconnect();
      }
  }
  ```

* 订阅模式SubPattern

  ```java
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
  
  ```



* 创建发布类 Pub

  ```java
  package org.xpp.redis;
  
  import redis.clients.jedis.Jedis;
  
  public class Pub {
      public static void main(String[] args) {
          System.out.println("消息发布端");
          Jedis jedis = new Jedis("linux1", 6379);
          jedis.publish("channel1", "message1");
          jedis.publish("pattern0", "pattern0");
          // 模拟取消订阅
          jedis.publish("pattern1", "unsubscribe");
          jedis.disconnect();
      }
  }
  
  ```



* 启动Sub

  ![订阅channel](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722152216028.png)

* 启动subPattern

  ![image-20210722152356409](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722152356409.png)

* 此时能看到两个客户端启动着

  ![image-20210722153229361](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722153229361.png)



* 启动Pub

  查看Sub窗口：

  ![image-20210722153327458](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722153327458.png)

  可以看到message1消息和unsubscribe消息都被接受到。并且channel1的订阅被取消。

  查看SubPattern窗口：

  ![image-20210722153508223](https://raw.githubusercontent.com/xpp1109/images/main/uPic/image-20210722153508223.png)

  消息也被正常接收。

* 完结撒花~~~




