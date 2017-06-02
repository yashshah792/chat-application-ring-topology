
package chatroom;


import java.io.Serializable;
import java.net.InetAddress;
import java.util.ArrayList;
/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/

public class UICommunicator implements Serializable{
    
    private static final long serialVersionUID = 1L;
    public String type, sender, content, receiver;
    public ArrayList<String> dataList;
    InetAddress IPSender,IPReceiver;
    
    
    public UICommunicator(String type, String sender, String content, String recipient){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
    }
    public UICommunicator(String type, String sender, String content, String recipient,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
        this.IPSender=tsenderIp;
        this.IPReceiver=treceiverIp;
    }
    public UICommunicator(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
        this.dataList=tdataStirng;
    }
    public UICommunicator(String type, String sender, String content, String recipient,ArrayList<String> tdataStirng,InetAddress tsenderIp, InetAddress treceiverIp){
        this.type = type; this.sender = sender; this.content = content; this.receiver = recipient;
        this.dataList=tdataStirng;
        this.IPSender=tsenderIp;
        this.IPReceiver=treceiverIp;
    }
    @Override
    public String toString(){
        return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+receiver+"'}";
    }
}
