package cbp.double0negative.xServer;

import java.io.File;
import java.util.HashMap;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.ChatColor;


import cbp.double0negative.xServer.Server.Server;
import cbp.double0negative.xServer.client.ChatListener;
import cbp.double0negative.xServer.client.Client;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;
/**
 * 
 * @
 * 
 * 
 * Authors:
 * @author Drew  [ https://github.com/Double0negative ]
 * @author James [ https://github.com/James-Buchanan ]
 * 
 */
public class XServer extends JavaPlugin{

    public static String version = "0.2.6";
    public static ChatColor color = ChatColor.WHITE;
    public static ChatColor seccolor = ChatColor.WHITE;
    public static ChatColor aColor = ChatColor.AQUA;
    public static ChatColor pColor = ChatColor.GOLD;
    public static ChatColor eColor = ChatColor.DARK_RED;
    public static String pre = "[XServer] ";
    public static String xpre = pColor+pre;
    public static String ip;
    public static  int port;
    public static String prefix;
    public static String serverName;
    public static boolean isHost = false;
    private Server server;
    private Client client;
    public static boolean netActive = true;
    public static int restartMode = 0;
    public static boolean dc = false;
    public static boolean hostdc = false;
    private static Player stat_req = null;
    private ChatListener cl = new ChatListener();
    public static HashMap<String, String>formats = new HashMap<String,String>();
    public static HashMap<String, String>override = new HashMap<String,String>();
    private static boolean formatoveride = false;


    public void onEnable(){

        netActive = true;
        LogManager log = LogManager.getInstance();
        log.setup(this);
        log.info("XServer Version "+version+" Initializing");


        getConfig().options().copyDefaults(true);
        this.saveDefaultConfig();
        ip = getConfig().getString("ip");
        port = getConfig().getInt("port");
        prefix = getConfig().getString("prefix");
        isHost = getConfig().getBoolean("host");
        serverName = getConfig().getString("serverName");

        formats.put("MESSAGE", getConfig().getString("formats.Message"));
        formats.put("LOGIN", getConfig().getString("formats.Login"));
        formats.put("LOGOUT", getConfig().getString("formats.Logout"));
        formats.put("DEATH", getConfig().getString("formats.Death"));
        formats.put("CONNECT", getConfig().getString("formats.Connect"));
        formats.put("DISCONNECT", getConfig().getString("formats.Disconnect"));


        formatoveride = getConfig().getBoolean("override.enabled");
        override.put("MESSAGE", getConfig().getString("override.Message"));
        override.put("LOGIN", getConfig().getString("override.Login"));
        override.put("LOGOUT", getConfig().getString("override.Logout"));
        override.put("DEATH", getConfig().getString("override.Death"));
        override.put("CONNECT", getConfig().getString("override.Connect"));
        override.put("DISCONNECT", getConfig().getString("override.Disconnect"));




        if(isHost){
            LogManager.getInstance().info("THIS SERVER IS HOST");
            startServer();
        }

        startClient();

        this.getServer().getPluginManager().registerEvents(cl, this);
    }
    String s = "";

    public void onDisable()
    {

        hostdc = false;
        netActive = false;
        dc = false;

        if(restartMode == PacketTypes.DC_TYPE_RELOAD){
            s = "Reload";
        }
        else if(restartMode == PacketTypes.DC_TYPE_STOP){
            s = " Shutting Down";
        }
        dc();
        if(isHost)
            dcServer();

    }

    public void startClient(){
        if(!dc){
            client = new Client(this,ip,port);
            client.openConnection();
            cl.setClient(client);
        }
    }

    public void dc(){
        if(!dc){
            client.send(new Packet(PacketTypes.PACKET_MESSAGE, aColor + prefix +" Disconnecting. "+((!s.equals(""))?"Reason: "+s:"")));
            client.stopClient();
            this.getServer().broadcastMessage(aColor+pre+"Disconnecting from host");


        }




    }

    public void reloadClient(){
        dc = false;
        dc();
        startClient();
    }

    public void startServer(){
        if(!hostdc){
            LogManager.getInstance().info("Starting as Host");
            server = new Server();
            server.start();
            netActive = true;
        }

    }

    public void dcServer(){
        if(!hostdc){
            Server.sendPacket(new Packet(PacketTypes.PACKET_MESSAGE, eColor +"[XServer] Host Disconnecting."), null);
            server.closeConnections();
        }
    }

    public void reloadServer(){
        hostdc = false;
        netActive = false;
        dc();
        dcServer();
        startClient();
        startServer();
    }

    public boolean onCommand(CommandSender sender, Command cmd1, String commandLabel, String[] args){
        String cmd = cmd1.getName();
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }
        
        if(cmd.equalsIgnoreCase("xserver") || cmd.equalsIgnoreCase("x")){ 
            if(args[0].equalsIgnoreCase("list")){
                stat_req = player;
                getStats();
            }
            if(player.isOp()){
                if(args[0].equalsIgnoreCase("dc") || args[0].equalsIgnoreCase("disconnect")){
                    if(dc){
                        player.sendMessage(xpre+"Already Disconnected!");
                    }
                    else{
                        dc();
                        dc = true;

                        player.sendMessage(xpre+"Disconnected. You will be reconnected on next restart or with /x rc");
                    }
                }
                if(args[0].equalsIgnoreCase("rc") || args[0].equalsIgnoreCase("reload")){
                    reloadClient();
                    player.sendMessage(xpre+"Client Restarted");
                }
                if(args[0].equalsIgnoreCase("v") || args[0].equalsIgnoreCase("version")){
                    player.sendMessage(xpre+"Version: "+version);
                }

                if(args[0].equalsIgnoreCase("host") || args[0].equalsIgnoreCase("server")){
                    if(args[1].equalsIgnoreCase("dc") || args[1].equalsIgnoreCase("disconnect")){
                        if(!isHost){
                            player.sendMessage(xpre+"You are not host!");
                        }
                        else if(hostdc){
                            player.sendMessage(xpre+"Already Disconnected!");
                        }
                        else {
                            hostdc = true;
                            dcServer();
                            player.sendMessage(xpre+"Server Shutdown! Restarting on next restart or with /x host rc");
                        }
                    }
                    if(args[1].equalsIgnoreCase("rc") || args[1].equalsIgnoreCase("reload") || args[1].equalsIgnoreCase("reconnect")){
                        if(!isHost){
                            player.sendMessage(xpre+"You are not host!");
                        }
                        else {
                            reloadServer();
                            player.sendMessage(xpre+"Server Restarted!");
                        }

                    }
                }
            }

            return true;
        }
        return false;
    }

    public void getStats(){
        client.send(new Packet(PacketTypes.PACKET_STATS_REQ, null));
    }

    public static void msgStats(Object[][] stats){
        stat_req.sendMessage(pColor+"--------------XServer Chat Stats----------------");
        stat_req.sendMessage(pColor+"Server      Active      Packets Sent            Packets Recived");
        for(Object[] o:stats){
            String name = addspaces((String)o[0],25);
            String active = addspaces((Boolean) (o[1])?"true":"false",30);
            String sent = addspaces(o[2]+"",40);
            String rec = addspaces(o[3]+"",7);
            stat_req.sendMessage(pColor+name+active+sent+rec);
        }

    }

    public static String addspaces(String s, int sp){
        for(int a = 0;a< sp-s.length(); a++){
            s = s+" ";
        }
        return s;
    }

    public static String format(   HashMap<String, String>format, HashMap<String, String> val, String key){
        String str = "";
        if(!formatoveride){
            str = format.get(key);
        }else{
            str = override.get(key);
        }

        str = str.replaceAll("\\{message\\}",(val.get("MESSAGE") != null)? val.get("MESSAGE"): "");
        str = str.replaceAll("\\{username\\}", (val.get("USERNAME") != null)? val.get("USERNAME"): "");
        str = str.replaceAll("\\{server\\}", (val.get("SERVERNAME") != null)? val.get("SERVERNAME"): "");

        str = str.replaceAll("(&([a-f0-9]))", "\u00A7$2");
       
        return str;
    }

}
