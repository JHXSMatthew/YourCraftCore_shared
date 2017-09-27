package com.mcndsj.YourCraftCore.player;

import com.mcndsj.YourCraftCore.party.Party;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by Matthew on 2016/4/19.
 */
@Setter @Getter
public class Player {
    String name;
    String inLobby = null;

    boolean Vip = false;
    boolean offline = false;

    Party party = null;

    public Player(String name, String lobby, boolean Vip){
        this.name = name;
        this.inLobby = lobby;
        this.Vip = Vip;
    }

    public boolean hasParty(){
        return party == null;
    }

}
