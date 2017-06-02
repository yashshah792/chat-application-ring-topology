
package chatroom;
import java.io.*;
import java.net.*;
import java.util.*;
/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class ChatroomBase{
    public ArrayList<Socket> chtSocket;
    public ArrayList<String> usrInfo;
    public String chtrmName;
    
    ChatroomBase(String tname){
        usrInfo = new ArrayList<String>();
        chtSocket = new ArrayList<Socket>();
        chtrmName=tname;
    }
    
}
