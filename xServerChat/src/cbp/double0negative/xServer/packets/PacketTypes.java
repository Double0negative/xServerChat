package cbp.double0negative.xServer.packets;

public class PacketTypes {

	public static int PACKET_SERVER_DC	    = 1;
	public static int PACKET_SERVER_NAME    = 2;
	public static int PACKET_AUTH 		    = 3;
	public static int PACKET_MESSAGE	    = 4;
	public static int PACKET_STATS_REQ 		= 5;
	public static int PACKET_STATS_REPLY    = 6;
	public static int PACKET_PING           = 7;
	public static int PACKET_PONG           = 8;
	public static int PACKET_CLIENT_DC	    = 9;
	public static int PACKET_CC             = 12;
	public static int PACKET_PLAYER_JOIN    = 10;
	public static int PACKET_PLAYER_LEAVE   = 11;  
	public static int PACKET_PLAYER_DEATH   = 13;  

	public static int DC_TYPE_RELOAD = 1;
	public static int DC_TYPE_STOP = 2;
}
