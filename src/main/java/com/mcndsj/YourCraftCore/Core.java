package com.mcndsj.YourCraftCore;

import com.mcndsj.YourCraftCore.queueSystem.BalancerController;
import com.mcndsj.YourCraftCore.commandLine.*;
import lombok.Getter;

import java.io.IOException;

/**
 * Created by Matthew on 2016/4/19.
 */
@SuppressWarnings("ALL")
@Getter
public class Core {

    public static boolean running = true;


    CommandManager cm = new CommandManager();
    BalancerController balancerControl = null;

    Console console = null;


    public static Core instance = null;
    public Core(){
        instance = this;
        //setup IO
        console = new Console(cm);
        STDOUT.setUp();
        //setup features
        STDOUT.print(STDOUT.level.INFO,"Server starting ...",0);
        setUp();
        STDOUT.print(STDOUT.level.INFO, "Start up done  loading configs.",0);
        STDOUT.print(STDOUT.level.INFO, "loading configs.",0);

        loadConfig();
        STDOUT.print(STDOUT.level.INFO, "Load Config done.",0);
        STDOUT.print(STDOUT.level.INFO, "Listen on Console!",0);

        openConsole();
    }

    private void loadConfig(){
        balancerControl.loadConfig();
    }

    private void openConsole(){
        try {
            console.run();
        } catch (IOException e) {
            e.printStackTrace();
        }
        /*
            try {
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);
                String line = "";
                while((line = br.readLine()) != null && !line.equals("exit")){
                    StringTokenizer token = new StringTokenizer(br.readLine(), " ");
                    String label = token.nextToken();
                    ArrayList<String> args = new ArrayList<String>();
                    while(token.hasMoreTokens()){
                        args.add(token.nextToken());
                    }
                    cm.command(label, (String[])args.toArray());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
    }

    private void setUp(){
        balancerControl = new BalancerController();
        STDOUT.print(STDOUT.level.INFO, " - balancer start up.",0);
        cm = new CommandManager();
        STDOUT.print(STDOUT.level.INFO, " - command manager start up.",0);
    }

    public void shutDown(){

        Core.getInstance().running = false;
        balancerControl.close();
        System.out.println("Bye~");
        System.exit(1);
    }

    public static Core getInstance(){
        return instance;
    }

    public BalancerController getBalancer(){
        return balancerControl;
    }

    public static void main(String [] args){
        Core core = new Core();

    }
}
