package com.mcndsj.YourCraftCore.queueSystem.GameBalancer;

import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.BalancerFactory;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.ServerACK;
import com.mcndsj.YourCraftCore.queueSystem.ServerInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Matthew on 20/06/2016.
 */
public class GameServerACK extends ServerACK {



    public GameServerACK(BalancerFactory balancer){
        super(balancer);
    }

    @Override
    public void onNewServerCreate(JSONObject json, ServerInfo info) {
        int current = ((Long) json.get("online")).intValue();
        int max = ((Long) json.get("max")).intValue();
        STDOUT.print(STDOUT.level.INFO, "Game created [" + info.getTypeName() + info.getId()+ "]"   +  " "+current+"/"+max ,1);

    }

    @Override
    public void onServerUpdate(JSONObject json, ServerInfo info) {
        int current = ((Long) json.get("online")).intValue();
        int max = ((Long) json.get("max")).intValue();

        STDOUT.print(STDOUT.level.INFO, "Game update [" + info.getTypeName() + info.getId()+ "]"   +  " "+current+"/"+max ,1);

        if(current >= max || current < 0 ) {
            getBalancer().removeServer(info);
            STDOUT.print(STDOUT.level.INFO, "Server left list [" + info.getTypeName() + info.getId() + "]",1);
        } else {
            info.alive();
        }
    }
}
