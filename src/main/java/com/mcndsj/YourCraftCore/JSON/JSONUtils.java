package com.mcndsj.YourCraftCore.JSON;

import com.mcndsj.YourCraftCore.player.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Created by Matthew on 2016/4/19.
 */
public class JSONUtils {

    /**
     * String name
     * String lobby
     * boolean vip
     *
     * @param s
     * @return
     * @throws Exception
     */
    public static Player unpackPlayerJson(String s) throws Exception {
        JSONParser json = new JSONParser();
        try{
            Object obj = json.parse(s);
            JSONObject map = (JSONObject )obj;
            return new Player((String)map.get("name"),(String) map.get("lobby"),(Boolean)map.get("vip"));
        }catch(ParseException e){
            e.printStackTrace();
        }
        throw new Exception("Cannot Parse player");
    }



}
