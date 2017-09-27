package com.mcndsj.YourCraftCore;

import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * Created by Matthew on 2016/4/19.
 */
public class JedisUtils {
    private static JedisPool pool =null;

    public static redis.clients.jedis.Jedis get(){
        if(pool == null){
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(2048);
            pool = new JedisPool(config,"192.168.123.2",6379,0,"NO_PUBLIC_INFO");

        }
        if(pool.getNumActive() > 30 || pool.getNumWaiters() > 0){
            STDOUT.print(STDOUT.level.WARN, "Pool size warm, active: " + pool.getNumActive()+ " waiters:" + pool.getNumWaiters()  ,0);
        }
        return pool.getResource();
    }

    public static void publish(String channel, String msg){
        Jedis jedis = get();
        jedis.publish(channel,msg);
        jedis.close();
    }
}
