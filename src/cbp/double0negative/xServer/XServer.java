package cbp.double0negative.xServer;

import java.util.logging.Logger;

import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.*;

import org.bukkit.ChatColor;


import cbp.double0negative.xServer.Server.Server;
import cbp.double0negative.xServer.client.ChatListener;
import cbp.double0negative.xServer.client.Client;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;

public class XServer extends JavaPlugin{

	public static String version = "0.0.1";
	public static String xpre = ChatColor.GOLD+"[XServer] ";

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

		if(isHost){
			startServer();
		}

		startClient();

		this.getServer().getPluginManager().registerEvents(cl, this);

	}

	public void onDisable(){
		String s = "";
		if(restartMode == PacketTypes.DC_TYPE_RELOAD){
			s = "Reload";
		}
		else if(restartMode == PacketTypes.DC_TYPE_STOP){
			s = " Shutting Down";
		}
		if(!dc){
			client.send(new Packet(PacketTypes.PACKET_MESSAGE, ChatColor.DARK_RED + "[XServer]" +serverName+ " Disconnecting. " + ((!s.equals(""))? "Reason: "+s : "")));

			netActive = false;
			client.closeConnection();
		}
		if(isHost && !hostdc){
			Server.sendPacket(new Packet(PacketTypes.PACKET_MESSAGE, ChatColor.DARK_RED +"[XServer] Host Disconnecting. " + s), null);
			server.closeConnections();
		}

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
			client.send(new Packet(PacketTypes.PACKET_MESSAGE, ChatColor.DARK_RED + "[XServer]" +serverName+ " Disconnecting."));
			client.closeConnection();
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
		}
	}

	public void dcServer(){
		if(!hostdc){
			Server.sendPacket(new Packet(PacketTypes.PACKET_MESSAGE, ChatColor.DARK_RED +"[XServer] Host Disconnecting."), null);
			server.closeConnections();
		}
	}

	public void reloadServer(){
		hostdc = false;
		dc();
		dcServer();
		startClient();
		startServer();
	}

	public boolean onCommand(org.bukkit.command.CommandSender sender, org.bukkit.command.Command cmd1, String commandLabel, String[] args){
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



				if(args[0].equalsIgnoreCase("host") || args[0].equalsIgnoreCase("server")){
					if(args[1].equalsIgnoreCase("dc") || args[1].equalsIgnoreCase("disconnect")){
						if(!isHost){
							player.sendMessage(xpre+"You are not host!");
						}
						else if(hostdc){
							player.sendMessage(xpre+"Already Disconnected!");
						}
						else {
							dcServer();
							hostdc = true;

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
		stat_req.sendMessage(ChatColor.MAGIC+"--------------XServer Chat Stats----------------");
		stat_req.sendMessage(ChatColor.YELLOW+"Server            Packets Sent            Packets Recived");
		for(Object[] o:stats){
			String name = addspaces((String)o[0],40);
			String sent = addspaces(o[2]+"",40);
			String rec = addspaces(o[3]+"",7);
			stat_req.sendMessage(ChatColor.YELLOW+name+sent+rec);
		}

	}

	public static String addspaces(String s, int sp){
		for(int a = 0;a< sp-s.length(); a++){
			s = s+" ";
		}
		return s;
	}
}
