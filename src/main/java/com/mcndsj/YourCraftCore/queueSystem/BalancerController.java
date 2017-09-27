package com.mcndsj.YourCraftCore.queueSystem;

import com.mcndsj.YourCraftCore.Core;
import com.mcndsj.YourCraftCore.commandLine.STDOUT;
import com.mcndsj.YourCraftCore.player.Player;
import com.mcndsj.YourCraftCore.queueSystem.GameBalancer.GameBalancer;
import com.mcndsj.YourCraftCore.queueSystem.IBalancer.BalancerFactory;
import com.mcndsj.YourCraftCore.queueSystem.lobbyBalancer.LobbyBalancer;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Matthew on 2016/4/19.
 */
public class BalancerController {
    private List<BalancerFactory> balancer;

    public BalancerController() {
        balancer = new ArrayList<BalancerFactory>();
    }

    public BalancerFactory getBalancer(String typeName){
        for(BalancerFactory b : balancer){
            if(b.getType().equals(typeName)){
                return b;
            }
        }
        return null;
    }

    public void loadConfig(){
        Yaml yaml = new Yaml();
        try {
            List<String> list;
            list = yaml.loadAs(new FileReader(new File("balance.yml")),ArrayList.class);
            for(String s : list){
                addBalancer(s,BalancerType.game);
            }
        } catch (FileNotFoundException e) {
            STDOUT.print(STDOUT.level.ERROR, "Config Error, cannot find balance.yml!",0);
        }
        finally{
            //do io close
        }

        yaml = new Yaml();
        try {
            List<String> list;
            list = yaml.loadAs(new FileReader(new File("lobby.yml")),ArrayList.class);
            for(String s : list){
                addBalancer(s,BalancerType.lobby);
            }
        } catch (FileNotFoundException e) {
            STDOUT.print(STDOUT.level.ERROR, "Config Error, cannot find balance.yml!",0);
        }
        finally{
            //do io close
        }

        //fix father balancer stuff
        LobbyBalancer balancer = (LobbyBalancer) getBalancer("lobby");
        for(BalancerFactory fc : this.balancer){
            if(fc instanceof  LobbyBalancer){
                if(!fc.getType().equals(balancer.getType())){
                    ((LobbyBalancer) fc).setFather(balancer);
                }
            }
        }
    }


    public void close(){
        Iterator<BalancerFactory> iterator = balancer.iterator();
        while(iterator.hasNext()){
            BalancerFactory b = iterator.next();
            b.destroy();;
            iterator.remove();
        }
    }

    public void addBalancer(String typeName,BalancerType type ){
        if(getBalancer(typeName) != null){
            STDOUT.print(STDOUT.level.WARN, "try to add exsisting balancer " + typeName,0);
            return;
        }

        if(type == BalancerType.game) {
            balancer.add(new GameBalancer(typeName));
        }else if(type == BalancerType.lobby){
            balancer.add(new LobbyBalancer(typeName));
        }else{
            return;
        }
        STDOUT.print(STDOUT.level.INFO, "add new balancer "+ typeName + " " + type.toString(),0);

    }

    public void removeBalancer(String typeName){
        getBalancer(typeName).destroy();
        balancer.remove(getBalancer(typeName));
        STDOUT.print(STDOUT.level.INFO," remove balancer " + typeName,0);
    }

    public void removePlayerFromLobby(Player p){
        for(BalancerFactory bf : balancer){
            if(bf instanceof LobbyBalancer){
                bf.removePlayer(p);
            }
        }
    }

    public List<BalancerFactory> getAllBalancers(){
        return balancer;
    }

    public void print(String name){
        for(BalancerFactory b : Core.getInstance().getBalancer().getAllBalancers()){
            if(b.getType().contains(name)){
                STDOUT.print(STDOUT.level.INFO , "- " + b.getType(),0);
                STDOUT.print(STDOUT.level.INFO , " - Players: "+ b.getQueueCount()+ "/" + b.getCount(true),0);
                STDOUT.print(STDOUT.level.INFO, " - Servers: " + b.getServerInfoString(),0);
            }
        }
    }

    public void print(){
        for(BalancerFactory b : Core.getInstance().getBalancer().getAllBalancers()){
            STDOUT.print(STDOUT.level.INFO , "- " + b.getType(),0);
            STDOUT.print(STDOUT.level.INFO , " - Players: "+ b.getQueueCount()+ "/" + b.getCount(true),0);
            STDOUT.print(STDOUT.level.INFO, " - Servers: " + b.getServerInfoString(),0);
        }
    }
 }
