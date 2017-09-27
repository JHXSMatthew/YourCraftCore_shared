package com.mcndsj.YourCraftCore.queueSystem;

/**
 * Created by Matthew on 20/06/2016.
 */
public enum BalancerType {
    game("game"),lobby("lobby");
    String name;

    BalancerType(String name){
        this.name = name;
    }

    public static BalancerType nameToType(String ask){
        for(BalancerType type : values()){
            if(ask.equals(type.name)){
                return type;
            }
        }
        return null;
    }
}
