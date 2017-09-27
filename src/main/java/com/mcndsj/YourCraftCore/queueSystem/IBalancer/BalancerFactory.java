package com.mcndsj.YourCraftCore.queueSystem.IBalancer;

import com.mcndsj.YourCraftCore.JedisUtils;
import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import com.mcndsj.YourCraftCore.player.Player;
import com.mcndsj.YourCraftCore.queueSystem.Queues.MyQueue;
import com.mcndsj.YourCraftCore.queueSystem.Queues.ServerListQueue;
import com.mcndsj.YourCraftCore.queueSystem.ServerInfo;
import com.mcndsj.YourCraftCore.queueSystem.ServerInfoCom_online;
import lombok.Getter;
import lombok.Setter;
import org.json.simple.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Matthew on 20/06/2016.
 */
@Getter
@Setter
public abstract class BalancerFactory {

    private String type;
    private int max;
    private int SLEEP_PERTICK = 50;

    private ExecutorService pool ;
    private Timer timer;

    private List<ServerInfo> serverList;
    private MyQueue playerQueue;
    public  List<JedisPubSub> allPubSubs;

    private boolean ERROR = false;

    private QueueOperationFactory queueOperationFactory;
    private ServerACK serverACK;

    public BalancerFactory(final String typeName){
        this.type = typeName;
        pool = Executors.newCachedThreadPool();
        timer = new Timer();

        allPubSubs = new ArrayList<JedisPubSub>();
        serverList = new ServerListQueue();
        playerQueue = new MyQueue(typeName);

        register();

        //run main thread
        timer.schedule(runMainThread() ,SLEEP_PERTICK,SLEEP_PERTICK);
    }

    /**
     *
     *  LobbyServerACK
     */

    public void register(){
        this.serverACK  = registerGameACK();
        this.queueOperationFactory = registerQueueFactory();

        allPubSubs.add(getQueueOperationFactory());
        allPubSubs.add(getServerACK());

        pool.execute(new Runnable() {

            public void run() {
                try (Jedis j  =  JedisUtils.get()) {
                    STDOUT.print(STDOUT.level.INFO , "ready to subscribe(" + getQueueOperationFactory().getJoinChannel() + "." + type+")",3);
                    j.subscribe(getQueueOperationFactory(), getQueueOperationFactory().getJoinChannel() + "." + type);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        pool.execute(new Runnable() {
            public void run() {
                try (Jedis j  =  JedisUtils.get()) {
                    STDOUT.print(STDOUT.level.INFO , "ready to subscribe(GameACK" +  "." + type+")",3);
                    j.subscribe(getServerACK(), "GameACK." + type);
                    System.err.println("UN SUBSCRIEB GAME ACK"  + type);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        pool.execute(new Runnable() {
            public void run() {
                try (Jedis j  =  JedisUtils.get()) {
                    j.subscribe(getQueueOperationFactory(),  getQueueOperationFactory().getLeavingChannel() + "." + type);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
    }

    public abstract TimerTask runMainThread();

    public abstract ServerACK registerGameACK();

    public abstract QueueOperationFactory registerQueueFactory();


    //=========================================================================================================================================
    /**
     * Core operations
     */
    protected void publishCount(){
        JedisUtils.publish("QueueCount." + type, String.valueOf(getCount(false)));
    }



    public abstract void  doJoinInstance();

    protected void doJob() throws InterruptedException {
        doJoinInstance();
    }
    //=========================================================================================================================================



    protected static String getStackMsg(Exception e) {

        StringBuffer sb = new StringBuffer();
        StackTraceElement[] stackArray = e.getStackTrace();
        for (int i = 0; i < stackArray.length; i++) {
            StackTraceElement element = stackArray[i];
            sb.append(element.toString() + "\n");
        }
        return sb.toString();
    }


    public void sendPlayerTo(Player player, ServerInfo server){
        JSONObject obj = new JSONObject();
        obj.put("name",player.getName());
        obj.put("server",  server.getTypeName()+ server.getId());
        JedisUtils.publish("ServerSend."+player.getInLobby(),obj.toJSONString());
        STDOUT.print(STDOUT.level.INFO, "Send " + player.getName() + " ["+ player.getInLobby() +"]->[" + server.getTypeName() + server.getId() + "]" ,3);
    }


    public int getQueueCount(){
        return playerQueue.getCount();
    }

    public int getCount(boolean inner){
        if(inner) {
            return playerQueue.getCount() + getServerWaitCount() ;
        }
        if(serverList.isEmpty()){
            return -1;
        }
        return playerQueue.getCount() + getServerWaitCount() ;
    }

    private int getServerWaitCount(){
        int count = 0;
        for(ServerInfo info : serverList){
            count += info.getOnline();
        }
        return count;
    }

    public ServerInfo getInfoFronId(int id){
        for(ServerInfo info : serverList) {
            if(info.getId() == id) {
                return info;
            }
        }
        return null;
    }

    public String getServerInfoString(){
        StringBuilder builder = new StringBuilder();

        for(ServerInfo info : serverList){
            ;builder.append("**");
            builder.append(info.getId())
                    .append(": ")
                    .append(info.getOnline())
                    .append("/")
                    .append(info.getMax());
            builder.append("** ");
        }
        return builder.toString();
    }

    public void addServer(ServerInfo info){
        synchronized (serverList) {
            serverList.add(info);
            Collections.sort(serverList,Collections.reverseOrder(new ServerInfoCom_online()));
        }
    }

    public void removeServer(ServerInfo info){
        synchronized (serverList) {
            serverList.remove(info);
            Collections.sort(serverList,Collections.reverseOrder(new ServerInfoCom_online()));
        }
    }

    public void removePlayer(Player p){
        synchronized (serverList) {
            playerQueue.remove(p);
        }
    }

    public void addPlayer(Player p){
        synchronized (serverList) {
            playerQueue.add(p);
        }
    }



    public void destroy(){
        for(JedisPubSub ps : allPubSubs){
            try {
                ps.unsubscribe();
            }catch(Exception e){

            }
        }
        pool.shutdown();
        timer.cancel();
    }


}


