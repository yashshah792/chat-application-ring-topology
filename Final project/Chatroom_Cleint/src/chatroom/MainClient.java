
package chatroom;

import java.net.ServerSocket;
import java.net.Socket;

/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class MainClient {
    public static void main(String args[]) {
        try{
            final int serverPort = 9010;
            ServerSocket ser_sock = new ServerSocket(serverPort);
            
            UIHome loginScreenObj = new UIHome();
            loginScreenObj.setVisible(true);
            
            while(true){
                Socket cli_sock = ser_sock.accept();
                System.out.println("connection accepted");
                TempServer newconn = new TempServer(cli_sock,loginScreenObj.client);
                
                Thread tobj = new Thread(newconn);
                
                tobj.start();
                System.err.println("after creating temp server");
                
            }
        }catch(Exception e){
            System.out.println("Kya avyu aa:"+e);
        }
        
        
    }
    
}
