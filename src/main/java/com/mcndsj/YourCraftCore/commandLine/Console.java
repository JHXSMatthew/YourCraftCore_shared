package com.mcndsj.YourCraftCore.commandLine;

import com.mcndsj.YourCraftCore.Core;
import jline.console.ConsoleReader;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by Matthew on 2016/4/21.
 */
public class Console {

    ConsoleReader reader;
    PrintWriter out;
    CommandManager manager;
    public Console(CommandManager manager)  {
        this.manager = manager;
        try{
            reader = new ConsoleReader();
            reader.setPrompt(">");
            out = new PrintWriter(reader.getOutput());
        }catch(Exception e){

        }
    }

    public void run() throws IOException {
        String line = "";
        try {
            line = reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(!line.equals("exit")) {
            if(line != null) {
                StringTokenizer token = new StringTokenizer(line, " ");
                if (!token.hasMoreTokens()) {
                    try {
                        line = reader.readLine();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                String label = token.nextToken();
                ArrayList<String> args = new ArrayList<String>();
                while (token.hasMoreTokens()) {
                    args.add(token.nextToken());
                }
                String[] str = args.toArray(new String[args.size()]);
                if(!manager.command(label, str)){
                    STDOUT.print(STDOUT.level.ERROR,"Unknown command !",0);
                }
            }
            line =  reader.readLine();
        }
        STDOUT.print(STDOUT.level.INFO, "Shutting down...",0);
        Core.getInstance().shutDown();
    }

    public PrintWriter getOut(){
        out.flush();
        return this.out;
    }


}
