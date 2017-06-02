package chatroom;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class TempClient implements Runnable{
    public Socket socket;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public TempClient(InetAddress serverIp){
        try{
            System.err.println("trying to connect"+serverIp.getHostAddress());
            String ipAddress = serverIp.getHostAddress();
            socket = new Socket(ipAddress, 9010);
            
            objOut = new ObjectOutputStream(socket.getOutputStream());
            objOut.flush(); 
        }catch(Exception e){
            System.err.println("Exception in ClientSideTempClient"+e);
        }
    }
    public void sendToServer(UICommunicator msg){
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing message in clientSideTempClient: "+msg);
        }catch(Exception e){
            System.out.println("Exception in chatroom_client:"+e);
        }
        
    }

    @Override
    public void run() {
        
    }
}
