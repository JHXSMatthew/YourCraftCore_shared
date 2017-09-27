package com.mcndsj.YourCraftCore.queueSystem;

import java.util.Comparator;

/**
 * Created by Matthew on 1/07/2016.
 */
public class ServerInfoCom_online implements Comparator<ServerInfo> {
    @Override
    public int compare(ServerInfo o1, ServerInfo o2) {
        if(o1.getOnline() == o1.getMax() || o1.getOnline() < 0){
            return -1;
        }else if(o2.getOnline() == o2.getMax() || o2.getOnline() < 0){
            return 1;
        }else {
            return o1.getOnline() - o2.getOnline();
        }
    }
}
