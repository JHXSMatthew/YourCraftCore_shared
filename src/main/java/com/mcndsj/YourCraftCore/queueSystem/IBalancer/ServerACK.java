package com.mcndsj.YourCraftCore.queueSystem.IBalancer;

import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import com.mcndsj.YourCraftCore.queueSystem.ServerInfo;
import lombok.Getter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.JedisPubSub;

/**
 * Created by Matthew on 20/06/2016.
 */
public abstract class ServerACK extends JedisPubSub {

    @Getter
    private BalancerFactory balancer;

    public ServerACK(BalancerFactory balancer){
        this.balancer = balancer;
    }


    @Override
    public void onMessage(String channel, String message) {
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(message);
            JSONObject json = (JSONObject) obj;
            if(!json.get("type").equals(balancer.getType())){
                return;
            }

            ServerInfo info = balancer.getInfoFronId((((Long) json.get("id")).intValue()));
            if(info == null){
                if(((Long) json.get("online")).intValue() >= 0) {
                        info = new ServerInfo((String)json.get("type"),
                            (((Long) json.get("id")).intValue()),
                            (((Long) json.get("online")).intValue()),
                            (((Long) json.get("max")).intValue()));
                    balancer.addServer(info);
                    onNewServerCreate(json,info);
                }
            }else{
                info.setLastACK(System.currentTimeMillis());
                info.setOnline((((Long) json.get("online")).intValue()));

                if(((Long) json.get("online")).intValue() < 0){
                    getBalancer().removeServer(info);
                }else {
                    onServerUpdate(json, info);
                }

                balancer.setMax(info.getMax());
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    public abstract void onNewServerCreate(JSONObject json,ServerInfo info);

    public abstract void onServerUpdate(JSONObject json, ServerInfo info);

}
