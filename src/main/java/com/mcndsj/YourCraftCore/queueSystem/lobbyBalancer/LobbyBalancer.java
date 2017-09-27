package com.mcndsj.YourCraftCore.queueSystem.lobbyBalancer;

import com.mcndsj.YourCraftCore.Core;
import com.mcndsj.YourCraftCore.JedisUtils;
import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import com.mcndsj.YourCraftCore.player.Player;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.BalancerFactory;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.QueueOperationFactory;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.ServerACK;
import com.mcndsj.YourCraftCore.queueSystem.ServerInfo;

import javax.jws.Oneway;
import java.util.*;

/**
 * Created by Matthew on 20/06/2016.
 */
public class LobbyBalancer extends BalancerFactory {

    private LobbyBalancer father;

    public LobbyBalancer(String typeName) {
        super(typeName);
    }


    public void setFather(LobbyBalancer father){
        this.father = father;
    }

    @Override
    public TimerTask runMainThread() {
        return new TimerTask() {
            int count = 0;
            @Override
            public void run() {
                try {
                    doJob();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                count ++;
                if(count > 20){
                    count = 0;
                    publishCount();
                }
            }
        };
    }

    @Override
    public ServerACK registerGameACK() {
        return new LobbyServerACK(this);
    }

    @Override
    public QueueOperationFactory registerQueueFactory() {
        return new LobbyQueueOperation("lobbyJoin","lobbyQuit",this);
    }

    @Override
    public void doJoinInstance() {
        STDOUT.print(STDOUT.level.DEBUG, "Balancer " + getType() + " Wake up! Queue Size = " + getQueueCount() ,3);
        int count  = 0;
        synchronized (getPlayerQueue()){
            synchronized (getServerList()) {
                try {
                    Iterator<ServerInfo> iter = getServerList().iterator();
                    while (iter.hasNext()) {
                        ServerInfo current = iter.next();

                        if (current.shouldClean()) {
                            iter.remove();
                            STDOUT.print(STDOUT.level.WARN, " Server [" + current.getTypeName() + current.getId() + "] track has been lost.",3);
                            continue;
                        }
                        int amount = current.getMax() - current.getOnline();
                        if (amount == 0)
                            continue;

                        List<Player> list = getPlayerQueue().giveMe(amount);
                        if (list.isEmpty()) {
                            STDOUT.print(STDOUT.level.DEBUG, "no player in the playerQueue!",3);
                            break;
                        }
                        current.setOnline(current.getOnline() + list.size());
                        count++;
                        JedisUtils.publish("PlayerSend." + getType() + current.getId(), String.valueOf(list.size()));
                        for (Player p : list) {
                            sendPlayerTo(p, current);
                        }
                    }
                    //vip op
                    if(!getServerList().isEmpty()) {
                        List<Player> vips = getPlayerQueue().giveMeVIPs();
                        if(!vips.isEmpty()){
                            Random r = new Random();
                            for (Player p : vips) {
                                ServerInfo current = getServerList().get(r.nextInt(getServerList().size()));
                                JedisUtils.publish("PlayerSend." + getType() + current.getId(), String.valueOf(1));
                                sendPlayerTo(p, current);
                            }
                        }
                    }

                    if(!getPlayerQueue().isEmpty() && father != null){
                        List<Player> players= getPlayerQueue().giveMe(getPlayerQueue().getSize());
                        for(Player p : players){
                            father.addPlayer(p);
                        }
                    }

                } catch (ConcurrentModificationException e) {
                    STDOUT.print(STDOUT.level.ERROR, getStackMsg(e),1);
                }

            }
        }
        if(count != 0)
            STDOUT.print(STDOUT.level.INFO, "Balancer " + "[" + getType() + "] has balanced " + count + " players before Sleep",3);

        STDOUT.print(STDOUT.level.DEBUG, "Balancer " + getType() + " Sleep!. Queue Size = " + getQueueCount() ,3);
    }

    @Override
    public void sendPlayerTo(Player player, ServerInfo server){

        super.sendPlayerTo(player,server);
        //Core.getInstance().getBalancer().removePlayerFromLobby(player);
    }

    @Override
    public void addServer(ServerInfo info){
        synchronized (getServerList()) {
            getServerList().add(info);
            Collections.sort(getServerList());
        }
    }


}
