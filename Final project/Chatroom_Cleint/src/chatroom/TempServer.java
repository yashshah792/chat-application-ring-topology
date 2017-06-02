
package chatroom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;

/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class TempServer implements Runnable{
    Socket clientSocket;
    ObjectInputStream objIn;
    ObjectOutputStream objOut;
    ChtrmCli client;
    public TempServer(Socket cliSocket,ChtrmCli tclient) throws IOException{
        clientSocket = cliSocket;
        client = tclient;
        objIn= new ObjectInputStream(clientSocket.getInputStream());
        objOut= new ObjectOutputStream(clientSocket.getOutputStream());
    }
    
    @Override
    public void run(){
        try{
            try{
                while(true){
                    if(objIn.available()<0&&objIn!=null){
                        return;
                    }
                    UICommunicator message = (UICommunicator)objIn.readObject();
                    System.out.println(message);
                    if(message.type.equals("tempJoin")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"** stop here....");
                        if(storedServerIp==null){
                            // for the second member of the ringtopology
                            client.tmpClient = new TempClient(senderIp);
                            Thread obj1 = new Thread(client.tmpClient);
                            obj1.start();
                            client.IP_clientsideServer=senderIp;
                            System.err.println("************New ClientSideServerIp"+senderIp);
                            UICommunicator msg = new UICommunicator("ringCompletion", client.username,"first member joined last member", "lastMember", receiverIp, senderIp);
                            client.tmpClient.sendToServer(msg);
                        }else if(!storedServerIp.getHostAddress().equals(receiverIp.getHostAddress())){
                            client.tmpClient.sendToServer(message);
                        }else if(storedServerIp.getHostAddress().equals(receiverIp.getHostAddress())){
                            
                            client.IP_clientsideServer = senderIp;
//                            client.clientTemp.socket.close();
                            client.tmpClient = new TempClient(senderIp);
                            Thread obj1 = new Thread(client.tmpClient);
                            obj1.start();
                            UICommunicator msg = new UICommunicator("ringCompletion", client.username,"first member joined last member", "lastMember", receiverIp, senderIp);
                            client.tmpClient.sendToServer(msg);
                        }
                    }else if(message.type.equals("ringCompletion")){
                        //do nothing. It's just an acknoledge
                    }else if(message.type.equals("tempGetChatroomList")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            System.err.println("I am not super peer. I will pass message to my local server");
                            client.tmpClient.sendToServer(message);
                        }else if(storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            System.err.println("I am the super peer and i have to give chatroom list");
                            ArrayList<String> chatroomNameWithNum = new ArrayList<String>();
                            for(int i=0;i<client.chatroomnames.size();i++){
                                String tempObjct = client.chatroomnames.get(i)+"("+client.noOfClientsInGrp.get(i)+")";
                                chatroomNameWithNum.add(tempObjct);
                            }
                            System.err.println(chatroomNameWithNum);
                            client.tmpClient.sendToServer(new UICommunicator("tempGetChatroomListAns", "localMainServer", "listOfchatroomList", "localNewClient", chatroomNameWithNum, client.IP_clientsideServer, senderIp));
                        }
                    }else if(message.type.equals("tempGetChatroomListAns")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
                        chatroomListNameWithNum=message.dataList;
                        System.err.println(chatroomListNameWithNum+"----------------");
                        client.chatroomnames=new ArrayList<String>();
                        client.noOfClientsInGrp=new ArrayList<Integer>();
                        for(int i=0;i<chatroomListNameWithNum.size();i++){
                            int indexOfOpenBreket = chatroomListNameWithNum.get(i).indexOf('(');
                            int indexOfCloseBreket = chatroomListNameWithNum.get(i).indexOf(')');
                            String trueName = chatroomListNameWithNum.get(i);
                            
                            trueName=trueName.substring(0, indexOfOpenBreket);
                            System.err.println("truename:"+trueName);
                            client.chatroomnames.add(trueName);
                            Integer numOfClient = Integer.parseInt(chatroomListNameWithNum.get(i).substring(indexOfOpenBreket+1, indexOfCloseBreket));
                            client.noOfClientsInGrp.add(numOfClient);
                        }
                        System.err.println("chatroomname:"+client.chatroomnames);
                        System.err.println("chatroommember:"+client.noOfClientsInGrp);
                        if(client.chtrm!=null){
                            client.chtrm.chatroomList(client.chatroomnames,client.noOfClientsInGrp);
                        }
                        
                    }else if(message.type.equals("joinGroup")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            client.tmpClient.sendToServer(message);
                        }
                        if(client.chtrm!=null){
                            ArrayList<String> chatroomListNameWithNum = new ArrayList<String>();
                            chatroomListNameWithNum=message.dataList;
                            System.err.println(chatroomListNameWithNum+"----------------");
                            client.chatroomnames=new ArrayList<String>();
                            client.noOfClientsInGrp=new ArrayList<Integer>();
                            for(int i=0;i<chatroomListNameWithNum.size();i++){
                                int indexOfOpenBreket = chatroomListNameWithNum.get(i).indexOf('(');
                                int indexOfCloseBreket = chatroomListNameWithNum.get(i).indexOf(')');
                                String trueName = chatroomListNameWithNum.get(i);
                                
                                trueName=trueName.substring(0, indexOfOpenBreket);
                                System.err.println("truename:"+trueName);
                                client.chatroomnames.add(trueName);
                                Integer numOfClient = Integer.parseInt(chatroomListNameWithNum.get(i).substring(indexOfOpenBreket+1, indexOfCloseBreket));
                                client.noOfClientsInGrp.add(numOfClient);
                                
                            }
                            client.chtrm.chatroomList(client.chatroomnames,client.noOfClientsInGrp);
                        }
                    }else if(message.type.equals("userListInGroup")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        InetAddress addr = InetAddress.getByName(InetAddress.getLocalHost().getHostAddress());
                        if(!client.IP_client.getHostAddress().equals(senderIp.getHostAddress())){
                            String groupName = message.receiver;
                            for(int i=0;i<client.activeChtrm.size();i++){
                                UIChat tobj = client.activeChtrm.get(i);
                                System.out.println(tobj.chatWindowName);
                                if(tobj.chatWindowName.equals(groupName)){
                                    ArrayList<String> userInChatList = message.dataList;
                                    userInChatList.add(client.username);
                                    message.dataList=userInChatList;
                                    ArrayList<String> currentUserInChatroom = tobj.connectedUsers;
                                    currentUserInChatroom.add(message.sender);
                                    tobj.update(currentUserInChatroom);
                                    tobj.addmessage(message.sender+" has joined");
                                    break;
                                }
                            }
                            client.tmpClient.sendToServer(message);
                        }else{
                            String groupName = message.receiver;
                            
                            ArrayList<String> userInChatList = message.dataList;
                            int indexOf = userInChatList.indexOf(client.username);
                            userInChatList.set(indexOf, "You");
                            for(int i=0;i<client.activeChtrm.size();i++){
                                UIChat tobj = client.activeChtrm.get(i);
                                System.out.println(tobj.chatWindowName);
                                if(tobj.chatWindowName.equals(groupName)){
                                    tobj.update(userInChatList);
                                    break;
                                }
                            }
                        }
                    }else if(message.type.equals("tempMessage")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        String groupName = message.receiver;
                        for(int i=0;i<client.activeChtrm.size();i++){
                            UIChat tobj = client.activeChtrm.get(i);
                            System.out.println(tobj.chatWindowName);
                            if(tobj.chatWindowName.equals(groupName)){
                                tobj.addmessage(message.sender+"::"+message.content);
                                break;
                            }
                        }
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            client.tmpClient.sendToServer(message);
                        }
                    }else if(message.type.equals("leaveGroup")){
                        InetAddress senderIp = message.IPSender;
                        InetAddress receiverIp=message.IPReceiver;
                        InetAddress storedServerIp = client.IP_clientsideServer;
                        System.err.println("**"+senderIp+"**"+receiverIp+"**"+storedServerIp+"**");
                        String groupName = message.receiver;
                        for(int i=0;i<client.activeChtrm.size();i++){
                            UIChat tobj = client.activeChtrm.get(i);
                            System.out.println(tobj.chatWindowName);
                            if(tobj.chatWindowName.equals(groupName)){
                                tobj.addmessage(message.sender+" left. :-(");
                                break;
                            }
                        }
                        int indexOfChatroom = client.chatroomnames.indexOf(groupName);
                        System.err.println("index of chatroom:"+indexOfChatroom+"Number of users in that group"+client.noOfClientsInGrp.get(indexOfChatroom));
                        int numberOfClientInChatroom = client.noOfClientsInGrp.get(indexOfChatroom)-1;
                        client.noOfClientsInGrp.set(indexOfChatroom, numberOfClientInChatroom);
                        client.chtrm.chatroomList(client.chatroomnames, client.noOfClientsInGrp);
                        if(!storedServerIp.getHostAddress().equals(senderIp.getHostAddress())){
                            client.tmpClient.sendToServer(message);
                        }
                    }
                    
                }
            }finally{
                clientSocket.close();
            }
            
        }catch(Exception e){
            System.out.println("ClientSideTempServer  exception`11:"+e);
        }
        
    }
}
