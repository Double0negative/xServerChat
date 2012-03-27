package cbp.double0negative.xServer.Server;

import java.io.DataInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.ArrayList;


import cbp.double0negative.xServer.XServer;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;


public class Server extends Thread{

	private static ArrayList<Connection>clients = new ArrayList<Connection>();
	ServerSocket skt2;
	Socket skt;
	private boolean closed = false;
	public void run(){

		try{
			skt2 = new ServerSocket(XServer.port);
		}catch(Exception e){}
		while(!closed){

			try {

				LogManager.getInstance().info("Ready for connections on port "+XServer.port);
				skt = skt2.accept();
				LogManager.getInstance().info("Connection made on port "+XServer.port);

				Connection c = new Connection(skt);
				clients.add(c);
				c.start();
				/*while(skt.isConnected()){
					try{
						Packet packet = (Packet)in.readObject();
						try{sleep(100);}catch(Exception e){}
					}catch(Exception e){}
				}
				in.close();
				skt.close();*/

			}
			catch(Exception e) {
				LogManager.getInstance().error("Exception in server"); e.printStackTrace();try{sleep(10000);}catch(Exception e2){}
			}
		}


	}
	public static void sendPacket(Packet p, Connection connection) {
		for(Connection c: clients){
			if(c!=connection){
				c.send(p);
			}
		}
	}

	public static void genAndSendStats(Connection c){
		System.out.println("Creating Stats");
		Object[][] stats = new Object[clients.size()][4];
		for(int a = 0; a<clients.size(); a++){
			Connection i = clients.get(a);
			stats[a][0] = i.getClientName();
			stats[a][1] = i.isOpen();
			System.out.println(i.getClientName()+i.isOpen());
			stats[a][2] = i.getSent();
			stats[a][3] = i.getRecived();
		}
		System.out.println("Sending stats");
		c.send(new Packet(PacketTypes.PACKET_STATS_REPLY, stats));
	}

	public void closeConnections(){
		closed = true;
		try{
			for(Connection c:clients){
				c.closeConnection();
				c.send(new Packet(PacketTypes.PACKET_SERVER_DC, null));
			}
			for(int a = 0; a<clients.size(); a++){
				clients.remove(a);
			}
			skt2.close();
			skt.close();
		}
		catch(Exception e){}
	}
	public static void closeConnection(Connection c){
		c.closeConnection();
		/*for(int a = 0; a<clients.size(); a++){
			if(!clients.get(a).isOpen()){
				clients.remove(a);
			}
		}*/
	}
}
