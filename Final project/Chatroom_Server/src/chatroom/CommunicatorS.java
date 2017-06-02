
package chatroom;

import java.io.Serializable;
import java.net.InetAddress;
import java.util.*;
/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/

public class CommunicatorS implements Serializable{
    
    private static final long serialVersionUID = 1L;
    public String type, sender, content, receiver;
    public ArrayList<String> data;
    InetAddress IPSender,IPReceiver;
    
    
    public CommunicatorS(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
    }
    public CommunicatorS(String type, String sender, String content, String recipient,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
        this.IPSender=tsenderIp;
        this.IPReceiver=treceiverIp;
    }
    public CommunicatorS(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
        this.data=tdataStirng;
    }
    public CommunicatorS(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
        this.data=tdataStirng;
        this.IPSender=tsenderIp;
        this.IPReceiver=treceiverIp;
    }
    @Override
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+receiver+"'}";
    }
}
