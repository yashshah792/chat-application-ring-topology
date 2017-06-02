
package chatroom;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.lang.*;
import java.sql.ResultSet;
import java.util.Scanner;
import java.util.ArrayList;


/*
Yash Shah 1001111714
Suyog Swami 1001119101
Vishal Dholakia 1001106182
*/
public class DataBaseConnection {
    public Connection con;
    DataBaseConnection(){
        try{
            con = DriverManager.getConnection("jdbc:mysql://localhost/chats","root","root");
        }catch(Exception e){
            System.out.println("Exception in Dbconnection file");
        }
        
    }
    public boolean login (String uname, String pwd){
       try{
            
            Statement stm = con.createStatement();
            
            ResultSet rs = stm.executeQuery("select * from users where usernm ='" + uname + "' and passwd='"+pwd+"'");
            if(rs.last()){
                System.out.println(rs.getString("usernm")+rs.getString("passwd"));
                return true;
            }else{
                return false;
            }
            
        }catch (Exception e){ 
            System.out.println("your got exception"+e);
        }
       return false;
   }

public boolean register(String uname, String pwd)
{
    try{
            boolean s;
            
            s = validate(uname);
            
            if (s == false)
            {
                Statement stmt = (Statement) con.createStatement(); 
                String insert = "INSERT INTO users(usernm,passwd) VALUES('" + uname + "','" + pwd + "')";
                stmt.executeUpdate(insert); 
                return true;
            }
            else { return false; }
            
            
        }catch (Exception e){ System.out.println("your got exception"+e);
                   return false;}
   
}

public boolean validate(String uname1)
{        
        try{
                
            Statement stm = con.createStatement();
            ResultSet rs = stm.executeQuery("select * from users where usernm='"+uname1+"'");
            System.out.println("result set:"+rs);
            if(rs.last()){
                System.out.println("data found");
                    return true;
            }else{
                System.out.print("data not found");
                return false;
            }
        }catch (Exception e){ 
            System.out.println("you have got exception"+e+"at dbconnection file");
        }
        return false;
}

public ArrayList<String> getChtrm(){
    ArrayList<String> arrlist = new ArrayList<String>();
    try{
        Statement stmt = (Statement)con.createStatement();
        String selectString = "Select * from chatrm";
        ResultSet rs = stmt.executeQuery(selectString);
        if(rs !=null){
            while(rs.next()){
                arrlist.add(rs.getString("chat_rm_nm"));
            }
        }
    }catch(Exception e){
        
    }
    return arrlist;
}
 
 public boolean createChtrm(String chatroom){
     try{
         
         Statement stmt = (Statement) con.createStatement();
         String insert = "INSERT INTO chatrm (chat_rm_nm)VALUES('"+chatroom+"')";
         stmt.executeUpdate(insert);
         return true;
        }catch(Exception e){
            System.out.println("create chat rooms exception" +e);
            return false;
        }
 }
} 

