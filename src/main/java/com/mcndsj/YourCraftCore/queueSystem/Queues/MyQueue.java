package com.mcndsj.YourCraftCore.queueSystem.Queues;

import com.mcndsj.YourCraftCore.player.Player;
import com.mcndsj.YourCraftCore.commandLine.STDOUT;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Matthew on 2016/4/19.
 */
public class MyQueue {

    private List<String> queue;
    private ConcurrentHashMap<String,Player> name_Player = null;
    private String type = null;

    public MyQueue(String type){
        this.type = type;
        queue = Collections.synchronizedList(new LinkedList<String>());
        name_Player = new ConcurrentHashMap<String,Player>();
    }



    public void insert(Player p, int position){
        if(position != 0 && queue.size() < position){
            position = queue.size();
        }
        queue.add(position,p.getName());
        name_Player.put(p.getName(),p);
    }


    public void add(Player p){
        if(queue.contains(p.getName())){
            return;
        }
        STDOUT.print(STDOUT.level.INFO, "player " + p.getName() + " Join ["+ type +"] playerQueue from lobby [" + p.getInLobby() + "]" ,3);

        if(p.isVip()) {
            for (String temp : queue) {
                int count = 0;
                if (name_Player.get(temp).isVip()) {
                    count++;
                } else {
                    insert(p, count);
                    return;
                }
            }
        }

        queue.add(p.getName());
        name_Player.put(p.getName(),p);


    }

    public int getCount(){
        return queue.size();
    }

    public void remove(Player p){
        if(!queue.contains(p.getName()))
            return;

        STDOUT.print(STDOUT.level.INFO, "player " + p.getName() + " left ["+ type +"] playerQueue from lobby [" + p.getInLobby() + "]" ,3);
        queue.remove(p.getName());
        name_Player.remove(p.getName());
    }

    //@TODO: consideration of tem join
    public List<Player> giveMe(int amount){
        List<Player> returnValue = new ArrayList<Player>();
        int counter = 0;
        Iterator<String> iter = queue.iterator();
        while(iter.hasNext() && counter < amount){
            returnValue.add(name_Player.remove(iter.next()));
            iter.remove();
            counter ++;
        }
        return returnValue;
    }

    public List<Player> giveMeVIPs(){
        List<Player> returnValue = new ArrayList<Player>();
        int counter = 0;
        Iterator<String> iter = queue.iterator();
        while(iter.hasNext()){
            String name = iter.next();
            Player p = name_Player.get(name);
            if(!p.isVip()){
                break;
            }
            returnValue.add(name_Player.remove(name));
            iter.remove();
            counter ++;
        }
        return returnValue;
    }

    public int getSize(){
        return queue.size();
    }

    public boolean isEmpty(){
        return queue.isEmpty();
    }

}
