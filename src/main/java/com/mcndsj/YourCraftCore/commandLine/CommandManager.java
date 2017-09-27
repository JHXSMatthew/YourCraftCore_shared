package com.mcndsj.YourCraftCore.commandLine;

import com.mcndsj.YourCraftCore.Core;
import com.mcndsj.YourCraftCore.queueSystem.BalancerType;

/**
 * Created by Matthew on 2016/4/19.
 */
public class CommandManager {
    public boolean command(String label,String[] args){
        try {
            if (label.equals("balance")) {
                if (args.length == 0) {
                    STDOUT.print(STDOUT.level.INFO, " show/add/remove <args>", 0);
                    return true;
                } else {
                    if (args[0].equals("add")) {
                        if (args.length < 3) {
                            STDOUT.print(STDOUT.level.INFO, "add <ServertypeName> <gameOrlobby>", 0);
                            return true;
                        }
                        if (BalancerType.nameToType(args[2]) == null) {
                            STDOUT.print(STDOUT.level.INFO, "invalid type!", 0);
                            return true;
                        }
                        Core.getInstance().getBalancer().addBalancer(args[1], BalancerType.nameToType(args[2]));
                    } else if (args[0].equals("remove")) {
                        Core.getInstance().getBalancer().removeBalancer(args[1]);
                    } else if (args[0].equals("show")) {
                        if(args.length < 2){
                            Core.getInstance().getBalancer().print();
                        }else{
                            Core.getInstance().getBalancer().print(args[1]);
                        }
                    }
                }
                return true;
            } else if (label.equals("debug")) {
                STDOUT.debug = !STDOUT.debug;
                return true;
            } else if (label.equals("level")) {
                if (args.length > 0) {
                    STDOUT.currentLevel = Integer.parseInt(args[0]);
                    STDOUT.print(STDOUT.level.INFO, "level now is" + STDOUT.currentLevel, 0);

                } else {
                    STDOUT.print(STDOUT.level.INFO, "level <level>", 0);

                }
                return true;
            }
            return false;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

}
