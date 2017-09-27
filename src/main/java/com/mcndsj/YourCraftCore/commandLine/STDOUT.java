package com.mcndsj.YourCraftCore.commandLine;

import com.mcndsj.YourCraftCore.Core;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.*;

/**
 * Created by Matthew on 2016/4/19.
 */
public class STDOUT {
    static SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
    static SimpleDateFormat formatYM = new SimpleDateFormat("YY-MM-dd-HH-mm-ss ");
    static Logger logger = Logger.getLogger("YourCraft");
    static boolean debug = false;
    static int currentLevel = 1;

    public static void print(level level, String info,int depth){
        if(level == level.DEBUG && !debug)
            return;
        if(depth > currentLevel){
            return;
        }
        Core.getInstance().getConsole().getOut().println( format.format(Calendar.getInstance().getTime())+ " ["+ level.toString()+"] " + info);
        logger.log(Level.INFO,formatYM.format(Calendar.getInstance().getTime())+ " ["+ level.toString()+"] " + info);
    }

    public enum level{
        WARN,INFO,ERROR,DEBUG;
    }

    public static void setUp(){

        int limit = 100000000; // 100 Mb
        try {
            FileHandler fh = new FileHandler(formatYM.format(Calendar.getInstance().getTime()) + ".log",limit , 1,true);
            for(Handler h : logger.getHandlers()){
                logger.removeHandler(h);
            }
            logger.setUseParentHandlers(false);
            logger.addHandler(fh);
            fh.setFormatter(new MyFormatter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
