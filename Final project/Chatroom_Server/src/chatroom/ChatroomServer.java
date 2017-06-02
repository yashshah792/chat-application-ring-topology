
package chatroom;
import java.net.*;
import java.util.*;
import java.io.*;
/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class ChatroomServer {
     
     public static Handler chtrmHandler;
    
    public static void main(String[] args) {
        // TODO code application logic here
        
        try{
            final int serverPort = 9999;
            ServerSocket ser_sock = new ServerSocket(serverPort);
            chtrmHandler = new  Handler();
            chtrmHandler.getChtrm();
            for(int i=0;i<chtrmHandler.chtrm.size();i++){
                ChatroomBase obj = (ChatroomBase)chtrmHandler.chtrm.get(i);
                System.err.println(obj.chtrmName);
            }
            while(true){
                Socket cli_sock = ser_sock.accept();
                
                System.out.println("connection accepted");
                
                ChatroomServerObj newconn = new ChatroomServerObj(cli_sock,chtrmHandler);
                
                Thread tobj = new Thread(newconn);
                
                tobj.start();
            }
        }catch(Exception e){
            System.out.println(e);
        }
    }
    
}
