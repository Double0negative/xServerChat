package cbp.double0negative.xServer.client;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cbp.double0negative.xServer.XServer;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;

public class ChatListener implements Listener{
	
	Client c;
	
	public void setClient(Client c){
		this.c = c;
	}

    @EventHandler(priority = EventPriority.MONITOR)
    public void handleChat(PlayerChatEvent event){
    	
    	c.sendMessage(event.getPlayer().getDisplayName()+": "+event.getMessage());
    	
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handleCommand(PlayerCommandPreprocessEvent 	event){
    	
    	if(event.getMessage().equalsIgnoreCase("/reload")){
    		XServer.restartMode = PacketTypes.DC_TYPE_RELOAD;
    	}
    	if(event.getMessage().startsWith("/stop")){
    		XServer.restartMode = PacketTypes.DC_TYPE_STOP;
    	}
    	
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerJoin(PlayerJoinEvent	event){
    	
    	c.send(new Packet(PacketTypes.PACKET_PLAYER_JOIN, XServer.prefix + " "+event.getPlayer().getDisplayName()));
    	
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerLeave(PlayerQuitEvent	event){
    	
    	c.send(new Packet(PacketTypes.PACKET_PLAYER_LEAVE, XServer.prefix + " "+event.getPlayer().getDisplayName()));
    	
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void handlePlayerLeave(PlayerDeathEvent	event){
    	
    	c.sendMessage(event.getEntity().getDisplayName() + " Died");
    	
    }
    
   
    
   /* @EventHandler(priority = EventPriority.HIGH)
    public void handleCommand(PlayerCommandPreprocessEvent event){
    	
    	if(event.getMessage().equalsIgnoreCase("/reload")){
    		XServer.restartMode = PacketTypes.DC_TYPE_RELOAD;
    	}
    	if(event.getMessage().startsWith("/stop")){
    		XServer.restartMode = PacketTypes.DC_TYPE_STOP;
    	}
    	
    }*/
}
