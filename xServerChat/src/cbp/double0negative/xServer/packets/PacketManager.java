package cbp.double0negative.xServer.packets;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
/*
 * 
 * 
 * NOT USED
 * 
 * 
 */


public class PacketManager extends Thread{
	PacketManager _instance = new PacketManager();
	private ConcurrentHashMap<Integer,Packet> packetQueue = new ConcurrentHashMap<Integer,Packet>();
	private boolean execRunning = false;
	private int index = 0;
	private int execIndex = 0;
	private PacketManager(){}
	
	
	public synchronized void addPacket(Packet p){
		packetQueue.put(index,p);
		checkAndRun();
		index++;
	}
	
	public synchronized void remove(int i){
		packetQueue.remove(i);
	}
	
	
	
	private void checkAndRun(){
		if(index>execIndex && !execRunning){
			this.start();
		}
	}
	
	
	public void run(){
		execRunning = true;
		while(index>execIndex){
			packetQueue.get(execIndex);
			
			
			
			
			execIndex++;
		}
		execRunning = false;
		
	}
	
	

}
