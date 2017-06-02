
package chatroom;
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class ChatroomServerObj implements Runnable{
    
    public static ArrayList<Socket> clientSockets = new ArrayList<Socket>();
    public static ArrayList<String> clients = new ArrayList<String>();
    public static ArrayList<ObjectInputStream> inputStreams = new ArrayList<ObjectInputStream>();
    public static ArrayList<ObjectOutputStream> outputStreams = new ArrayList<ObjectOutputStream>();
    public Socket s;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public CommunicatorS message;
    public DataBaseConnection dbcon;
    public String username;
    public Handler chtrmHandler;
    public ChatroomServerObj(Socket tsock,Handler chatroom){
        this.s=tsock;
        chtrmHandler = chatroom;
        try {
            objIn = new ObjectInputStream(s.getInputStream());
            objOut= new ObjectOutputStream(s.getOutputStream());
            objOut.flush();
            clientSockets.add(s);
            inputStreams.add(objIn);
            outputStreams.add(objOut);
            
            dbcon = new DataBaseConnection();
        } catch (IOException ex) {
            Logger.getLogger(ChatroomServerObj.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    @Override
    public void run(){
        try{
           try{
               while(true){
                   if(objIn.available()<0){
                       return;
                   }
                    message = (CommunicatorS)objIn.readObject();
                   
                   System.out.println(message);
                   
                   if(message.type.equals("connection")){
//                       Message m1 = new Message("connection", "server", "true", "test");
                       CommunicatorS m1;
                       System.err.println("number of objects in socket array:"+clientSockets.size());
                       if(clientSockets.size()==1){
                           m1 = new CommunicatorS("connection", "server", "true", "test", s.getInetAddress(), s.getInetAddress());
                       }else{
                           
                           int numberOfSocket = clientSockets.size();
                           Socket lastObj = clientSockets.get(numberOfSocket-2);
                           m1 = new CommunicatorS("connection", "server", "true", "test", s.getInetAddress(), lastObj.getInetAddress());
                       }
                       
                       objOut.writeObject(m1);
                       objOut.flush();
                       
                   }else if(message.type.equals("signup")){
                       //creating user
                       //fail
                       String username = message.sender;
                       String password = message.content;
                       boolean check = dbcon.register(username, password);
                       if(check){
                           System.out.println("Signup Successful");
                           this.username = username;
                           CommunicatorS m1 = new CommunicatorS("signup", "server", "true", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }else{
                           CommunicatorS m1 = new CommunicatorS("signup", "server", "false", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }
                       
                   }else if(message.type.equals("login")){
                       String username = message.sender;
                       String password = message.content;
                       boolean check = dbcon.login(username, password);
                       if(check){
                           System.out.println("Login Successful");
                           this.username=username;
                           CommunicatorS m1 = new CommunicatorS("login", "server", "true", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }else{
                           CommunicatorS m1 = new CommunicatorS("login", "server", "false", username);
                           objOut.writeObject(m1);
                           objOut.flush();
                       }
                   }else if(message.type.equals("create")){
                       String nameOfChatroom = message.content;
                       boolean success = chtrmHandler.addChtrm(nameOfChatroom);
                       CommunicatorS m1=null;
                       if(success){
                           m1 = new CommunicatorS("create", message.sender, "true", nameOfChatroom);
                       }else{
                           m1 = new CommunicatorS("create", message.sender, "false", nameOfChatroom);
                       }
                       objOut.writeObject(m1);
                       objOut.flush();

                   }else if(message.type.equals("message")){
                       String sender = message.sender;
                       String textMsg = message.content;
                       String recipient = message.receiver;
                       ChatroomBase askedChatroom = null;
                       for(int i=0;i<chtrmHandler.chtrm.size();i++){
                           ChatroomBase obj = chtrmHandler.chtrm.get(i);
                           if(obj.chtrmName.equals(recipient)){
                               
                               askedChatroom=(ChatroomBase)obj;
                               break;
                           }
                       }
                       CommunicatorS m1 = new CommunicatorS("message", sender, textMsg, recipient);
                       for(int i = 0;i<askedChatroom.chtSocket.size();i++){

                           Socket sock1 = (Socket)askedChatroom.chtSocket.get(i);
                           int indexOfSocket = clientSockets.indexOf(sock1);
                           ObjectOutputStream tobj = outputStreams.get(indexOfSocket);
                           tobj.writeObject(m1);
                           tobj.flush();
                       }
                       
                       
                   }else if(message.type.equals("leave")){
                       String from_which_chatroom = message.content;
                       String who_wants_to_leave = message.sender;
                       
                       for(int i=0;i<chtrmHandler.chtrm.size();i++){
                           ChatroomBase tcht_rm_obj = (ChatroomBase)chtrmHandler.chtrm.get(i);
                           if(tcht_rm_obj.chtrmName.equals(from_which_chatroom)){
                               System.out.println("Chatroom found");
                               int index = tcht_rm_obj.chtSocket.indexOf(s);
                               
                               for(int j=0;j<clientSockets.size();j++){
                                   Socket tsock = (Socket)clientSockets.get(j);
                                   ObjectOutputStream objOp = (ObjectOutputStream)outputStreams.get(j);
                                   objOp.writeObject(new CommunicatorS("leave", "server", who_wants_to_leave, from_which_chatroom));
                               }
                               tcht_rm_obj.chtSocket.remove(index);
                               tcht_rm_obj.usrInfo.remove(index);
                               break;
                           }
                       }
                   }else if(message.type.equals("logout")){
                       
                       objOut.writeObject(new CommunicatorS("logout", "server", "true", "user"));
                       objOut.flush();
                       int index = clientSockets.indexOf(s);
                       clientSockets.remove(index);
                       clients.remove(index);
                   }else if(message.type.equals("getChatroomList")){
                       ArrayList<String> chatroomNameList = new ArrayList<String>();
                       for(int i=0;i<chtrmHandler.chtrm.size();i++){
                           ChatroomBase baseObj = chtrmHandler.chtrm.get(i);
//                           String tempString = baseObj.chatroom_name+"("+baseObj.chatSocketArray.size()+")";
                           String tempString = baseObj.chtrmName;
                           chatroomNameList.add(tempString);
                       }
//                       Message m1 = new Message("server", "getChatroomList", "chatroomlist", uname, chatroomNameList);
                       CommunicatorS m1 = new CommunicatorS("getChatroomList", "server", "chatroomlist", username, chatroomNameList);
                       objOut.writeObject(m1);
                       objOut.flush();
                       
                   }else if(message.type.equals("join")){
                       ChatroomBase askedChatroom = null;
                       String askedChatroomName=message.content;
                       for(int i=0;i<chtrmHandler.chtrm.size();i++){
                           ChatroomBase obj = chtrmHandler.chtrm.get(i);
                           if(obj.chtrmName.equals(askedChatroomName)){
                               askedChatroom=(ChatroomBase)obj;
                               break;
                           }
                       }    
                       askedChatroom.chtSocket.add(s);
                       askedChatroom.usrInfo.add(username);

                       try{
                           CommunicatorS m1 = new CommunicatorS("join", "server", username, message.content);
                           for(int i = 0;i<clientSockets.size();i++){
                               
                               Socket sock1 = (Socket)clientSockets.get(i);
                               ObjectOutputStream tobj = outputStreams.get(i);
                               tobj.writeObject(m1);
                               tobj.flush();
                           }
                       }catch(Exception e){
                           System.out.println("Exception in join message:"+e);
                       }
                           
                   }else if(message.type.equals("chat_user_list")){
                       ChatroomBase askedChatroom = null;
                       String askedChatroomName=message.content;
                       System.out.println(askedChatroomName);
                       for(int i=0;i<chtrmHandler.chtrm.size();i++){
                           ChatroomBase obj = chtrmHandler.chtrm.get(i);
                           if(obj.chtrmName.equals(askedChatroomName)){
                               System.out.println("chat room found");
                               askedChatroom=(ChatroomBase)obj;
                               break;
                           }
                       }
                       objOut.writeObject(new CommunicatorS("chat_user_list", "server", username, message.content, askedChatroom.usrInfo));
                       objOut.flush();
                   }
                   
               }
           }finally{
               s.close();
           }
        }catch(Exception e){
            System.out.println("Server side exception:"+e);
        }
        
    }
    
}
