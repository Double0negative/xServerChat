package cbp.double0negative.xServer.Server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

import cbp.double0negative.xServer.XServer;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;
import cbp.double0negative.xServer.util.LogManager;
import cbp.double0negative.xServer.Server.Connection;


public class Server extends Thread
{

	private static ArrayList<Connection> clients = new ArrayList<Connection>();
	ServerSocket skt2;
	Socket skt;
	private boolean closed = false;

	public void run()
	{

		try
		{
			skt2 = new ServerSocket(XServer.port);
			LogManager.info("[SERVER] Connections on port "+XServer.port + " please");
		}
		catch (Exception e)
		{
			LogManager.error("[SERVER] Exception in server - could not open port " + XServer.port);
		}
		while (!closed)
		{

			try
			{

				LogManager.info("[SERVER] Awaiting connection...");
				skt = skt2.accept();
				LogManager.info("[SERVER] Connection made!");

				Connection c = new Connection(skt);
				c.start();
				/*
				 * while(skt.isConnected()){ try{ Packet packet =
				 * (Packet)in.readObject(); try{sleep(100);}catch(Exception e){}
				 * }catch(Exception e){} } in.close(); skt.close();
				 */

			} catch (Exception e)
			{
				LogManager.error("[SERVER] Exception in server");
				e.printStackTrace();
				try
				{
					sleep(10000);
				} catch (Exception e2)
				{
				}
			}
		}
		interrupt();
	}

	public static void sendPacket(Packet p, Connection connection)
	{
		//System.out.println("Sending packet " + p.toString());
		for (Connection c : clients)
		{
			//System.out.println("Sending packet to: " + c.getClientName());
			if (c != connection)
			{
				c.send(p);
			}
		}
		p = null;
	}
	
	public static void checkIfDupe(Packet p, Connection c)
	{
		for (Connection cl : clients)
		{
			if(c.getName().equalsIgnoreCase(cl.getName()))
			{
				// Idea here is to close the new connection.
				// might work better other way round however.
				System.out.println("[SERVER] Duplicate server name detected!");
				c.closeConnection();
			}
		}
		clients.add(c);
		sendPacket(p, c);
		p = null;
	}

	public static void genAndSendStats(Connection c)
	{
		System.out.println("[SERVER] Creating Stats");
		Object[][] stats = new Object[clients.size()][4];
		for (int a = 0; a < clients.size(); a++)
		{
			Connection i = clients.get(a);
			stats[a][0] = a + " " + i.getClientName();
			stats[a][1] = i.isOpen();
			stats[a][2] = i.getSent();
			stats[a][3] = i.getRecived();
		}
		System.out.println("[SERVER] Sending stats");
		c.send(new Packet(PacketTypes.PACKET_STATS_REPLY, stats));
		stats = null;
	}

	public void closeConnections()
	{
		closed = true;
		try
		{
			for (Connection c : clients)
			{
				c.send(new Packet(PacketTypes.PACKET_SERVER_DC, "SERC"));
				c.closeConnection();
				clients.remove(c);
			}
			skt2.close();
			skt.close();
			skt = null;
			skt2 = null;
		}
		catch (Exception e)
		{
			LogManager.error("[SERVER] Could not close connection!");
		}
	}

	public static void closeConnection(Connection c)
	{
		c.send(new Packet(PacketTypes.PACKET_SERVER_DC, "SERC"));
		c.closeConnection();
		clients.remove(c);
		/*
		for(int a = 0; a<clients.size(); a++)
		{
			if(!clients.get(a).isOpen())
			{
				clients.remove(a);
			}
		}
		*/
	}
}
