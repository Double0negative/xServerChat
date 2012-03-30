package cbp.double0negative.xServer.Server;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;


import cbp.double0negative.xServer.XServer;
import cbp.double0negative.xServer.packets.Packet;
import cbp.double0negative.xServer.packets.PacketTypes;

public class Connection extends Thread {

    ObjectInputStream in;
    ObjectOutputStream out;
    Socket skt;
    private boolean open = true;
    private String name = "";
    private int sent = 0;
    private int recived = 0;

    public Connection(Socket skt2) {

        this.skt = skt2;
        System.out.println("Adding Client");
        open = true;
    }

    public void run() {
        while (open) {
            try {
                in = new ObjectInputStream(skt.getInputStream());
                parse((Packet) in.readObject());
                sent++;
            } catch (Exception e) {}
        }

    }

    public void send(Packet p) {
        try {
            out = new ObjectOutputStream(skt.getOutputStream());
            out.writeObject(p);
            recived++;
        } catch (Exception e) {
        }
    }

    public void parse(Packet p) {

        if(p.getType() == PacketTypes.PACKET_CLIENT_CONNECTED){
            name = (String)p.getArgs();
            Server.sendPacket(p, this);
        }
        else if (p.getType() == PacketTypes.PACKET_STATS_REQ){
            System.out.println("REQ_STATS");
            Server.genAndSendStats(this);
        }
        else if(p.getType() == PacketTypes.PACKET_CLIENT_DC){
            Server.sendPacket(p, this);
            Server.closeConnection(this);
        }
        else {
            Server.sendPacket(p, this);
        }

    }


    public void closeConnection(){
        open = false;

        try{
            send(new Packet(PacketTypes.PACKET_CC, null));
            out.close();
            in.close();
            skt.close();
        }catch(Exception e){}
    }

    public boolean isOpen(){
        return open;
    }
    public String getClientName(){
        return name;
    }
    public int getSent(){
        return sent;
    }
    public int getRecived(){
        return recived;
    }
}
