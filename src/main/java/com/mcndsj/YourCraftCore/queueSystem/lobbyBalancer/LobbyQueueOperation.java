package com.mcndsj.YourCraftCore.queueSystem.lobbyBalancer;

import com.mcndsj.YourCraftCore.JSON.JSONUtils;
import com.mcndsj.YourCraftCore.player.Player;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.BalancerFactory;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.QueueOperationFactory;

/**
 * Created by Matthew on 20/06/2016.
 */
public class LobbyQueueOperation extends QueueOperationFactory {

    private BalancerFactory balancerFactory;

    public LobbyQueueOperation(String joinChannel, String leavingChannel, BalancerFactory balancer){
        super(joinChannel,leavingChannel);
        this.balancerFactory = balancer;
    }



    @Override
    public void onLeave(String message) {
        try {
            Player p = JSONUtils.unpackPlayerJson(message);
            balancerFactory.removePlayer(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onJoin(String message) {
        try {
            Player p = JSONUtils.unpackPlayerJson(message);
            balancerFactory.addPlayer(p);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
