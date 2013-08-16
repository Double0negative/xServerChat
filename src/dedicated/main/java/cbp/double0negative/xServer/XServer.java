package cbp.double0negative.xServer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import cbp.double0negative.xServer.Server.Server;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;

/**
 * 
 * @ *
 * 
 * Authors:
 * 
 * @author Drew [ https://github.com/Double0negative ]
 * @author Stoolbend [ https://github.com/Stoolbend ]
 * 
 */
public class XServer
{

	public static String version = "0.2.7";
	public static String pre = "[XServer] ";
	public static String xpre = pre;
	public static String ip;
	public static String prefix;
	public static String serverName;
	public static boolean isHost = false;
	private static Server server;
	public static boolean netActive = true;
	public static int restartMode = 0;
	public static boolean dc = false;
	public static boolean hostdc = false;
	public static HashMap<String, String> formats = new HashMap<String, String>();
	public static HashMap<String, String> override = new HashMap<String, String>();
	private static boolean formatoveride = false;
	public static int port = 33777;
	
	public static void main(String[] args)
	{

		netActive = true;
		LogManager.info("xServerChat Dedicated Server Version " + version + " Initializing");

		//new yaml config handler
		
		
		// some ability to log stuff
		LogManager.info("Starting Server");
		
		startServer();
		
		try{
		    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		    String s2 = bufferRead.readLine();
	 
		    onCommand(s2);
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}

	static String s = "";

	public static void onDisable()
	{

		hostdc = false;
		netActive = false;
		dc = false;

		if (restartMode == PacketTypes.DC_TYPE_RELOAD)
		{
			s = "Reload";
		} else if (restartMode == PacketTypes.DC_TYPE_STOP)
		{
			s = " Shutting Down";
		}
		dcServer();
	}

	public static void startServer()
	{
		if (!hostdc)
		{
			LogManager.info("Starting as Host");
			server = new Server();
			server.start();
			netActive = true;
		}

	}

	public static void dcServer()
	{
		if (!hostdc)
		{
			Server.sendPacket(new Packet(PacketTypes.PACKET_MESSAGE, "[XServer] Host Disconnecting."), null);
			server.closeConnections();
		}
	}

	public static void reloadServer()
	{
		hostdc = false;
		netActive = false;
		dcServer();
		startServer();
	}

	public static void onCommand(String input)
	{
		//TODO This isnt hooked upto anything YET, YET.
		// something to add later after testing.
		if (input.equalsIgnoreCase("stop"))
		{
			onDisable();
		}
		if (input.equalsIgnoreCase("list"))
		{
			// some method of showing stats on the server
			//getStats();
		}
		if (input.equalsIgnoreCase("dc")
				|| input.equalsIgnoreCase("disconnect"))
		{
			if (hostdc)
			{
				LogManager.info(xpre + "Already Disconnected!");
			}
			else
			{
				hostdc = true;
				dcServer();
				LogManager.info(xpre
						+ "Server Shutdown! Restarting on next restart or with /x host rc");
			}
		}
		if (input.equalsIgnoreCase("rc")
				|| input.equalsIgnoreCase("reload"))
		{
			reloadServer();
			LogManager.info(xpre + "Server Restarted!");
		}
		if (input.equalsIgnoreCase("v") || input.equalsIgnoreCase("version"))
		{
			LogManager.info(xpre + "Version: " + version);
			LogManager.info(xpre + "Dedicated server by Stoolbend");
		}
	}
	public static void msgStats(Object[][] stats)
	{
		System.out.println(
				 "-= xServer Chat - Dedicated Server v"+version+" =-");
		System.out.println(
				 "Server      Active      Packets Sent            Packets Recived");
		for (Object[] o : stats)
		{
			String name = addspaces((String) o[0], 25);
			String active = addspaces((Boolean) (o[1]) ? "true" : "false", 30);
			String sent = addspaces(o[2] + "", 40);
			String rec = addspaces(o[3] + "", 7);
			System.out.println(name + active + sent + rec);
		}

	}

	public static String addspaces(String s, int sp)
	{
		for (int a = 0; a < sp - s.length(); a++)
		{
			s = s + " ";
		}
		return s;
	}

	public static String format(HashMap<String, String> format,
			HashMap<String, String> val, String key)
	{
		String str = "";
		if (!formatoveride)
		{
			str = format.get(key);
		} else
		{
			str = override.get(key);
		}

		str = str.replaceAll("\\{message\\}",
				(val.get("MESSAGE") != null) ? val.get("MESSAGE") : "");
		str = str.replaceAll("\\{username\\}",
				(val.get("USERNAME") != null) ? val.get("USERNAME") : "");
		str = str.replaceAll("\\{server\\}",
				(val.get("SERVERNAME") != null) ? val.get("SERVERNAME") : "");

		str = str.replaceAll("(&([a-fk-or0-9]))", "\u00A7$2");

		return str;
	}

}
