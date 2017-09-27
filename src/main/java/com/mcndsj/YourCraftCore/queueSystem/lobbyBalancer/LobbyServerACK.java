package com.mcndsj.YourCraftCore.queueSystem.lobbyBalancer;

import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.BalancerFactory;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.ServerACK;
import com.mcndsj.YourCraftCore.queueSystem.ServerInfo;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Matthew on 20/06/2016.
 */
public class LobbyServerACK extends ServerACK {



    public LobbyServerACK(BalancerFactory balancer){
        super(balancer);
    }

    @Override
    public void onNewServerCreate(JSONObject json, ServerInfo info) {

    }

    @Override
    public void onServerUpdate(JSONObject json, ServerInfo info) {
        if(((Long) json.get("online")).intValue() >= getBalancer().getMax())
            info.suspend();
        else
            info.alive();
    }


}
