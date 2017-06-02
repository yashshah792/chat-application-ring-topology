
package chatroom;

import java.io.Serializable;
import java.util.ArrayList;
import java.net.*;

/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class Handler{
    public ArrayList<ChatroomBase> chtrm;
    DataBaseConnection dbcon;
    Handler(){
        //load all chatrooms from the database and add those into arrayList
        dbcon = new DataBaseConnection();
        chtrm = new ArrayList<ChatroomBase>();
        
        
    }
    public void getChtrm(){
        ArrayList<String> chtrmlist = dbcon.getChtrm();
        
        for(int i = 0;i<chtrmlist.size();i++){
            ChatroomBase obj = new ChatroomBase(chtrmlist.get(i));
            chtrm.add(obj);
            obj=null;
            
        }
    }
    public boolean addChtrm(String chatroom_name){
        boolean success = dbcon.createChtrm(chatroom_name);
        if(success){
            ChatroomBase obj = new ChatroomBase(chatroom_name);
            chtrm.add(obj);
            obj=null;
            return true;
        }else{
            return false;
        }        
    }
    public void addClientToChatroom(String chatroom_name,Socket tsock,String tusername){
        
    }
    public void leaveChatroom(String chatroom_name, Socket tsock){
        
    }
}
