package cbp.double0negative.xServer.packets;

import java.io.Serializable;

import cbp.double0negative.xServer.Server.Connection;

public class Packet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	int type;
	Connection c;
	Object args;
	
	
	public Packet(int type, Object args){
		
		this.type = type;
		this.args = args;
		
	}
	
	public void setConnection(Connection c){
		this.c = c;
	} 
	
	
	public int getType(){
		return type;
	}
	
	public Object getArgs(){
		return args;
	}
	
	
}
