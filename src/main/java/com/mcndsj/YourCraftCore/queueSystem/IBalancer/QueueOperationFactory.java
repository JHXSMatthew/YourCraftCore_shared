package com.mcndsj.YourCraftCore.queueSystem.IBalancer;

import com.mcndsj.YourCraftCore.JSON.JSONUtils;
import com.mcndsj.YourCraftCore.player.Player;
import lombok.Getter;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Matthew on 20/06/2016.
 */
public abstract  class QueueOperationFactory extends JedisPubSub {

    @Getter
    private String leavingChannel;
    @Getter
    private String joinChannel;

    public QueueOperationFactory(String joinChannel, String leavingChannel){
        this.leavingChannel = leavingChannel;
        this.joinChannel = joinChannel;
    }

    @Override
    public void onMessage(String channel, String message) {
        if(channel.startsWith(leavingChannel)){
            onLeave(message);
        }else if(channel.startsWith(joinChannel)){
            onJoin(message);
        }
    }

    public abstract void onLeave(String message);

    public abstract void onJoin(String message);
}
