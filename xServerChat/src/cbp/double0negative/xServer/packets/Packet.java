package cbp.double0negative.xServer.packets;

import java.io.Serializable;
import java.util.HashMap;

import cbp.double0negative.xServer.Server.Connection;

public class Packet implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private 
	int type;
	Connection c;
	Object args;
	
	 HashMap<String, String>formats = new HashMap<String, String>();

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
	
	public void setFormat( HashMap<String, String>formats){
	    this.formats = formats;
	}
	public     HashMap<String, String> getFormat(){
	    return formats;
	}


	
}
