package com.mcndsj.YourCraftCore.queueSystem;

import lombok.Getter;
import lombok.Setter;

import java.util.Comparator;

/**
 * Created by Matthew on 2016/4/19.
 */
@Getter @Setter
public class ServerInfo implements Comparator<ServerInfo>,Comparable{

    private String typeName;
    private int id;
    private int online;
    private int max;

    private boolean alive = true;
    private long lastACK;

    public ServerInfo(String typeName ,int id,int online,int max){
        this.typeName = typeName;
        this.id = id;
        this.max = max;
        this.online = online;
    }


    public void alive(){
        alive= true;
    }
    public void suspend(){
        alive= false;
    }

    public boolean shouldClean(){
        return !alive && lastACK > 0 && (System.currentTimeMillis() - lastACK)/1000 > 60;
    }

    public void join(){
        online ++;
    }

    public int compare(ServerInfo o1, ServerInfo o2) {
        return o1.getId() - o2.getId();
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof ServerInfo){
            return id - ((ServerInfo) o).id;
        }
        return -1;
    }
}
