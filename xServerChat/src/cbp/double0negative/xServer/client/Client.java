package cbp.double0negative.xServer.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import cbp.double0negative.xServer.XServer;
import cbp.double0negative.xServer.Server.Server;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.ChatColor;

public class Client extends Thread{

	private String ip;
	private int port;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket skt;
	private boolean open = false;
	private Plugin p;
	public Client(Plugin p,String ip, int port){
		this.ip = ip;
		this.port = port;
		this.p = p;
	}


	public void openConnection(){
		this.start();
	}

	public void run(){
		boolean error = false;
		while(XServer.netActive && !XServer.dc){
			try{
				LogManager.getInstance().info("Client connection to  "+ip+":"+port);
				skt = new Socket(ip, port);
				open = true;
				send(new Packet(PacketTypes.PACKET_SERVER_NAME, XServer.serverName));
				sendMessage(ChatColor.RED + " Connected");
				this.p.getServer().broadcastMessage("[XServer]Connected to host");
			}catch(Exception e){if(!error){LogManager.getInstance().error("Failed to create Socket - Client");}error=true;}
			while(open && !XServer.dc){
				try{
					in = new ObjectInputStream(skt.getInputStream());

					Packet p = (Packet)in.readObject();
					parse(p);
				}catch(Exception e){LogManager.getInstance().error("Could not read packet");if(open){this.p.getServer().broadcastMessage("[XServer]Lost Connection to Host");}open = false;closeConnection();}
			}
			try{sleep(10000);}catch(Exception e){}
		}
	}

	public void parse(Packet p){
		try{
			if(p.getType() == PacketTypes.PACKET_MESSAGE){
				sendLocalMessage((String)p.getArgs());
			}
			else if(p.getType() == PacketTypes.PACKET_STATS_REPLY){
				XServer.msgStats((Object[][])p.getArgs());
			}
			else if(p.getType() == PacketTypes.PACKET_SERVER_DC){
				open = false;
			}
			else if(p.getType() == PacketTypes.PACKET_PLAYER_JOIN || p.getType() == PacketTypes.PACKET_PLAYER_LEAVE){
				String s = (p.getType() == PacketTypes.PACKET_PLAYER_JOIN)? " joined the game": " left the game";
				sendLocalMessage((String)p.getArgs() + s);
			}

		}
		catch(Exception e){LogManager.getInstance().error("Malformed Packet");e.printStackTrace();}
	}
	public void sendLocalMessage(String s){
		p.getServer().broadcastMessage(s);
	}

	public void sendMessage(String s){
		s = XServer.prefix+s;
		send(new Packet(PacketTypes.PACKET_MESSAGE, s));
	}

	public void send(Packet p){
		try{
			out = new ObjectOutputStream(skt.getOutputStream());
			out.writeObject(p);


		}
		catch(Exception e){LogManager.getInstance().error("Couldn't send message");e.printStackTrace();
		}
	}

	public void closeConnection(){
		try{
			in.close();
			out.close();
			open = false;
		}

		catch(Exception e ){}
		sendMessage(XServer.prefix +" Closing Connection");
		send(new Packet(PacketTypes.PACKET_CLIENT_DC,null));
	}
}

