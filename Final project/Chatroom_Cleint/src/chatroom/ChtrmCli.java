
package chatroom;
import java.net.*;
import java.io.*;
import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class ChtrmCli implements Runnable{
    
    /**
     * @param args the command line arguments
     */
    
    
    public Socket socket;
    public UIHome ui;
    public ObjectInputStream objIn;
    public ObjectOutputStream objOut;
    public UIRegister registerUI;
    public UILogin loginUI;
    public String username;
    public UIJoinChatroom chtrm;
    public ArrayList<UIChat> activeChtrm;
    public InetAddress IP_clientsideServer;
    public InetAddress IP_client;
    TempClient tmpClient;
    ArrayList<String> chatroomnames;
    ArrayList<Integer> noOfClientsInGrp;
    
    public ChtrmCli(UIHome frame) throws Exception{
        ui = frame;
        socket = new Socket("10.226.49.238", 9999);
        objOut = new ObjectOutputStream(socket.getOutputStream());
        objOut.flush();
        objIn = new ObjectInputStream(socket.getInputStream());
        activeChtrm = new ArrayList<UIChat>();
    }
    
    @Override
    public void run() {
        while(true){
            try{
                UICommunicator message = (UICommunicator)objIn.readObject();
                System.out.println("Received msg:"+message);
                if(message.type.equals("connection")){
                    if(message.content.equals("true")){
                        System.out.println("connection successful");
                        InetAddress senderIp = message.IPSender;
                        IP_client = senderIp;
                        InetAddress receiverIp = message.IPReceiver;
                        System.err.println(senderIp+"------"+receiverIp);
                        if(senderIp.getHostAddress().equals(receiverIp.getHostAddress())){
                            //first member of ring topology
                            System.err.println("first member");
                        }else{
                            System.err.println("not the first member");
                            IP_clientsideServer=receiverIp;
                            tmpClient = new TempClient(receiverIp);
                            Thread obj1 = new Thread(tmpClient);
                            obj1.start();
                            UICommunicator m1 = new UICommunicator("tempJoin", username, "new temp client side connection creadted ", "clientsideServer", senderIp, receiverIp);
                            tmpClient.sendToServer(m1);
                        }
                    }else{
                        System.out.println("something went wrong");
                    }
                }else if(message.type.equals("signup")){
                    System.out.println("Signup message"+message);
                    if(message.content.equals("true")){
                        username = message.receiver;
                        registerUI.showChatroom(username);
                    }else{
                        registerUI.usernameExists();
                    }
                    registerUI=null;
                }else if(message.type.equals("login")){
                    System.out.println("Login message"+message);
                    if(message.content.equals("true")){
                        username = message.receiver;
                        loginUI.showChatroom(username);
                    }else{
                        loginUI.invalidCredentials();
                    }
                    loginUI=null;
                }else if(message.type.equals("create")){
                    if(message.content.equals("true")){
                        String creatorname = message.sender;
                        for(int i=0;i<chatroomnames.size();i++){
                            String tempString = chatroomnames.get(i);
                            if(tempString.compareToIgnoreCase(message.receiver)>0){
                                chatroomnames.add(i,message.receiver);
                                noOfClientsInGrp.add(i,0);
                                break;
                            }
                        }
                        enterGroup(message.receiver);

                    }else{
                        JOptionPane.showMessageDialog(null, "Something went wrong. Try again later.");
                    }
                    
                    
                }else if(message.type.equals("message")){
                    String sender = message.sender;
                    String text = message.content;
                    String chatroomname = message.receiver;
                    for(int i=0;i<activeChtrm.size();i++){
                        UIChat obj = activeChtrm.get(i);
                        if(obj.chatWindowName.equals(chatroomname)){
                            if(username.equals(sender)){
                                obj.addmessage("You::"+text);
                            }else{
                                obj.addmessage(sender+":::"+text);
                                
                            }
                            break;
                        }
                        
                    }
                    
                    
                }else if(message.type.equals("leave")){
                    String nameOfChatroom = message.receiver;
                    for(int i=0;i<activeChtrm.size();i++){
                        UIChat tobj = (UIChat)activeChtrm.get(i);
                        if(tobj.chatWindowName.equals(nameOfChatroom)){
                            if(message.content.equals(username)){
                                tobj.dispose();
                                activeChtrm.remove(tobj);
                            }else{
                                tobj.addmessage(message.content+" left chatroom");
                                tobj.updateListRemoveUserOn(message.content);
                            }
                            
                            break;
                        }
                    }
                    retrieveList(chtrm, new UICommunicator("getChatroomList", username, "getlist", "server"));
                }else if(message.type.equals("logout")){
                    if(message.content.equals("true")){
                        chtrm.dispose();
                        chtrm=null;
                        socket.close();
                        String[] args = {};
                        UIHome.main(args);
                        
                    }
                }else if(message.type.equals("getChatroomList")){
                    System.out.println("got chatroomlist from server");
                    chatroomnames = message.dataList;
                    noOfClientsInGrp =  new ArrayList<>();
                    for(int m=0;m<chatroomnames.size();m++){
                        noOfClientsInGrp.add(0);
                    }
                    chtrm.chatroomList(chatroomnames,noOfClientsInGrp);
                }else if(message.type.equals("join")){
                    
                    if(message.content.equals(username)){
                        //open the chatwindow
                        System.out.println("Add chat window for this user because he is joining this chatroom for the first time.");
                        UIChat chtWindow = new UIChat();
                        chtWindow.chatWindowName = message.receiver;
                        chtWindow.Setup(this,username);
                        chtWindow.setVisible(true);
                        activeChtrm.add(chtWindow);
                        chtWindow.addmessage("You joined");
                        send(new UICommunicator("chat_user_list", username, message.receiver, "server"));
                        
                    }else{
                        //notify all users.
                        for(int i=0;i<activeChtrm.size();i++){
                            UIChat obj = activeChtrm.get(i);
                            if(obj.chatWindowName.equals(message.receiver)){
                                obj.addmessage(message.content+" joined");
                                obj.updateListAddUser(message.content);
                                break;
                            }
                            
                        }
                    }
                    retrieveList(chtrm, new UICommunicator("getChatroomList", username, "getlist", "server"));
                }else if(message.type.equals("chat_user_list")){
                    System.out.println("List of users "+message.dataList);
                    for(int i=0;i<activeChtrm.size();i++){
                        UIChat obj = activeChtrm.get(i);
                        if(obj.chatWindowName.equals(message.receiver)){
                            obj.update(message.dataList);
                            break;
                        }
                        
                    }
                }
                
            }catch(Exception e){
                System.err.println("Exception in chatroom_client:run method:"+e);
            }
        }
    }
    public void sendToServer(String messageText,String chatRoomName) throws UnknownHostException{
        InetAddress addr = InetAddress.getLocalHost();
        tmpClient.sendToServer(new UICommunicator("tempMessage", username, messageText, chatRoomName, IP_client, IP_clientsideServer));
    }
    public void enterGroup(String groupName) throws UnknownHostException{
        boolean x=true;
        for(int i=0;i<activeChtrm.size();i++){
            UIChat tobj = activeChtrm.get(i);
            System.out.println(tobj.chatWindowName);
            if(tobj.chatWindowName.equals(groupName)){
                System.err.println("it's already opened:");
                x=false;
                break;
            }
        }
        if(x){
            int indexOfChatroom = chatroomnames.indexOf(groupName);
            System.err.println("index of chatroom:"+indexOfChatroom+"Number of users in that group"+noOfClientsInGrp.get(indexOfChatroom));
            int numberOfClientInChatroom = noOfClientsInGrp.get(indexOfChatroom)+1;
            noOfClientsInGrp.set(indexOfChatroom, numberOfClientInChatroom);
            
            chtrm.chatroomList(chatroomnames, noOfClientsInGrp);
            UIChat chtWindow = new UIChat();
            chtWindow.chatWindowName = groupName;
            chtWindow.Setup(this,username);
            chtWindow.setVisible(true);
            activeChtrm.add(chtWindow);
            chtWindow.addmessage("You joined");
            if(IP_clientsideServer==null){
                ArrayList<String> nameOfUser = new ArrayList<String>();
                nameOfUser.add("You");
                chtWindow.update(nameOfUser);
            }else{
                ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
                for(int i=0;i<chatroomnames.size();i++){
                    String tempObj = chatroomnames.get(i)+"("+noOfClientsInGrp.get(i)+")";
                    chatroomListNameWithNum.add(tempObj);
                }
                
                tmpClient.sendToServer(new UICommunicator("joinGroup", username, "groupNameWithNum", "localServer", chatroomListNameWithNum, IP_client, IP_clientsideServer));
            }
            ArrayList<String> userName = new ArrayList<String>();
            userName.add(username);
            if(tmpClient!=null){
                tmpClient.sendToServer(new UICommunicator("userListInGroup", username, "nameList", groupName, userName, IP_client, IP_clientsideServer));
            }
            
        }else{
            JOptionPane.showMessageDialog(null, "Chatroom is already opened.");
        }
        
        
    }
    public void register(UIRegister frame, UICommunicator msg){
        registerUI=frame;
        send(msg);
    }
    public void logIn(UILogin frame, UICommunicator msg){
        loginUI=frame;
        send(msg);
    }
    public void logout(){
        for(int i=0;i<activeChtrm.size();i++){
            UIChat tobj = (UIChat)activeChtrm.get(i);
            send(new UICommunicator("leave", username, tobj.chatWindowName, "server"));
        }
        send(new UICommunicator("logout", username, "turnoff", "server"));
        
    }
    public void retrieveList(UIJoinChatroom tchtrm, UICommunicator msg){
        chtrm = tchtrm;
        send(msg);
    }
    public void enterChatroom(UIJoinChatroom tchtrm, UICommunicator msg){
        boolean x=true;
        for(int i=0;i<activeChtrm.size();i++){
            UIChat tobj = activeChtrm.get(i);
            System.out.println(tobj.chatWindowName);
            if(tobj.chatWindowName.equals(msg.content)){
                System.err.println("it's already opened:");
                x=false;
                break;
            }
        }
        if(x){
            chtrm = tchtrm;
            send(msg);
        }else{
            JOptionPane.showMessageDialog(null, "Chatroom is already opened.");
        }
        
    }
    public void exitGroup(String groupName){
        for(int i=0;i<activeChtrm.size();i++){
            UIChat tobj = activeChtrm.get(i);
            System.out.println(tobj.chatWindowName);
            if(tobj.chatWindowName.equals(groupName)){
                
                activeChtrm.remove(tobj);
                tobj.dispose();
                break;
            }
        }
        int indexOfChatroom = chatroomnames.indexOf(groupName);
        System.err.println("index of chatroom:"+indexOfChatroom+"Number of users in that group"+noOfClientsInGrp.get(indexOfChatroom));
        int numberOfClientInChatroom = noOfClientsInGrp.get(indexOfChatroom)-1;
        noOfClientsInGrp.set(indexOfChatroom, numberOfClientInChatroom);
        chtrm.chatroomList(chatroomnames, noOfClientsInGrp);
        
        if(IP_clientsideServer==null){
//            ArrayList<String> nameOfUser = new ArrayList<String>();
//            nameOfUser.add("You");
//            chtWindow.update(nameOfUser);
        }else{
            ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
            for(int i=0;i<chatroomnames.size();i++){
                String tempObj = chatroomnames.get(i)+"("+noOfClientsInGrp.get(i)+")";
                chatroomListNameWithNum.add(tempObj);
            }
            tmpClient.sendToServer(new UICommunicator("leaveGroup", username, "user left group", groupName, chatroomListNameWithNum, IP_client, IP_clientsideServer));
        }
    }
    public void send(UICommunicator msg){
        try {
            objOut.writeObject(msg);
            objOut.flush();
            System.out.println("Outgoing : "+msg);
        }catch(Exception e){
            System.out.println("Exception in chatroom_client:send method:"+e);
        }
        
    }
    
}