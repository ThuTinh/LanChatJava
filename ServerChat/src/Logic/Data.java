/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author Thu Tinh
 */
public class Data {
    private  SqlConnection sqlConnection;

    public Data() {
    sqlConnection = new SqlConnection();
    
    }
      public  void AddPrivateChat(String sender, String receive,Timestamp timeSend , String content) throws SQLException, ClassNotFoundException
    {
        try {
            sqlConnection.OpenConnection();
        String sql  = "Insert into PrivateChat(Sender, Receiver,TimeSend,Content) values(?,?,?,?)";
        PreparedStatement preparedStatement = sqlConnection.GetConnection().prepareStatement(sql);
        preparedStatement.setString(1,sender);
        preparedStatement.setString(2,receive );
        preparedStatement.setTimestamp(3, timeSend);
        preparedStatement.setString(4, content);
        preparedStatement.executeUpdate();
        sqlConnection.CloseConnection();
            
        } catch (Exception e) {
           // JOptionPane.showMessageDialog(null, "Lỗi dữ liệu");
        }
        
    }
    
       public String CheckLogin(String username, String password ) throws ClassNotFoundException, SQLException
   {
       sqlConnection.OpenConnection();
       String sql = "Select *from Author where username = ? and Passwords = ?";
       PreparedStatement preparedStatement = sqlConnection.GetConnection().prepareStatement(sql);
       preparedStatement.setString(1,username);
       preparedStatement.setString(2, password);
       ResultSet rs = preparedStatement.executeQuery();
       String name ="";
       if(rs.next())
       {
       name = rs.getString("Username");
       sqlConnection.CloseConnection();
       return name;
       }
       sqlConnection.CloseConnection();
       return "";
   }
    public boolean UserExists(String username) throws SQLException{
        try {
            sqlConnection.OpenConnection();
            String sql = "SELECT * FROM Author WHERE Username = ?";
            PreparedStatement pst = sqlConnection.GetConnection().prepareStatement(sql);
            pst.setString(1, username);
            ResultSet rs = pst.executeQuery();
            if (rs.next()){
               sqlConnection.CloseConnection();
                return true;
            } 
            else{
               sqlConnection.CloseConnection();
                return false;
            }    
        } catch (Exception ex) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex.toString());
        }
        sqlConnection.CloseConnection();
        return false;
    }
     public void AddUser(String username, String password){
        try {
           sqlConnection.OpenConnection();
            String sql = "INSERT INTO Author (Username, Passwords) values (?, ?)";
            PreparedStatement pst = sqlConnection.GetConnection().prepareStatement(sql);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();
            sqlConnection.CloseConnection();
        } catch (Exception ex) {
            //JOptionPane.showMessageDialog(null,"Lỗi dữ liệu");
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void SendHistory(String sender, String reviver, ServerThread serverThread) {
        try {
            sqlConnection.OpenConnection();
            String sql = "Select *from PrivateChat where Sender=? and Receiver=? or Sender=? and Receiver=? ";
            PreparedStatement preparedStatement = sqlConnection.GetConnection().prepareStatement(sql);
            preparedStatement.setString(1,sender);
            preparedStatement.setString(2,reviver);
            preparedStatement.setString(3,reviver);
            preparedStatement.setString(4,sender);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String msg =  rs.getString("Content").toString();
                if(sender.equals(rs.getString("Sender"))&& reviver.equals(rs.getString("Receiver")))
                {
                    serverThread.send(new Message("showHistory", sender, msg, reviver));
                }
                else
                {
                    if (reviver.equals(rs.getString("Sender"))&& sender.equals(rs.getString("Receiver"))) {
                        serverThread.send(new Message("showHistory", reviver, msg, sender));
                    }
                }        
            }
        } catch (Exception e) {
        }
    }
    
    public  boolean  CreateGroupChat(GroupChat groupChat)
    {
        try {
            sqlConnection.OpenConnection();
            String sql = "Insert into GroupInfo (NameGroup,Creator) values(?,?)";
            PreparedStatement ps = sqlConnection.GetConnection().prepareStatement(sql);
            ps.setString(1, groupChat.groupName);
            ps.setString(2, groupChat.lsClientName.get(0));
            if(ps.executeUpdate()==0)
            {
                sqlConnection.CloseConnection();
                return false;
            }
         Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            sql = "Insert into GroupMember (NameGroup, Member,Adder, TimeAdd) values(?,?,?,?)";
            ps = sqlConnection.GetConnection().prepareStatement(sql);
            ps.setString(1, groupChat.groupName);
            ps.setString(2, groupChat.lsClientName.get(0));
            ps.setString(3,groupChat.lsClientName.get(0) );
            ps.setTimestamp(4, now);
            if(ps.executeUpdate()==1)
            {
                sqlConnection.CloseConnection();
                return true;
            }
            else
            {
                sqlConnection.CloseConnection();
                return false;
            }
            
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi database tạo group:"+ e.toString());
        }
        return false;
    }
    
    public  boolean AddMemberGroup(String nameGroup,String member, String adder, Timestamp timeAdder ) throws SQLException, ClassNotFoundException
    {
        sqlConnection.OpenConnection();
        String sql = "Insert into GroupMember (NameGroup, Member, Adder, TimeAdd) values(?,?,?,?)";
        PreparedStatement ps = sqlConnection.GetConnection().prepareStatement(sql);
        ps.setString(1, nameGroup);
        ps.setString(2, member);
        ps.setString(3, adder);
        ps.setTimestamp(4, timeAdder);
        if(ps.executeUpdate()==1)
        {
            sqlConnection.CloseConnection();
            return true;
        }
        else
        {
            sqlConnection.CloseConnection();
            return false;
        } 
    }
    
    public boolean  AddGroupChatHistory(String sender , String nameGroup,String content, Timestamp t) throws SQLException, ClassNotFoundException
    {
        sqlConnection.OpenConnection();
        String sql = "Insert into GroupChat (NameGroup, Sender, Content, TimeSend) values(?,?,?,?)";
        PreparedStatement ps = sqlConnection.GetConnection().prepareStatement(sql);
        ps.setString(1, nameGroup);
        ps.setString(2, sender);
        ps.setString(3, content);
       
        ps.setTimestamp(4, t);
        if(ps.executeUpdate()==1)
        {
            sqlConnection.CloseConnection();
            return true;
        }
        else
        {
            sqlConnection.CloseConnection();
            return false;
        }
        
        
    }
    
    public  void SendGroupHistory(String nameGroup, ServerThread toMe)
    {
        try {
            sqlConnection.OpenConnection();
            String sql = "Select *from GroupChat where NameGroup = ?";
            PreparedStatement ps = sqlConnection.GetConnection().prepareStatement(sql);
            ps.setString(1, nameGroup);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
               String content = rs.getString("Content");
               toMe.send(new Message("GroupChatHistory", rs.getString("Sender"), content, nameGroup));
                
            }
        } catch (Exception e) {
           // JOptionPane.showMessageDialog(null, "Loi histoy ls : "+ e.toString());
           
        }
        
      
    }
    public boolean DeleteGroupChat(String groupName)
    {
        try {
            sqlConnection.OpenConnection();
            String sql = "Delete from GroupInfo where Name=?";
            PreparedStatement ps = sqlConnection.GetConnection().prepareStatement(sql);
            ps.setString(1, groupName);
            if( ps.executeUpdate()==1)
            {
                sqlConnection.CloseConnection();
                return true;
            }
            else
            {
                sqlConnection.CloseConnection();
                return false;
            }

        } catch (Exception e) {
        }
        return false;
    }
    
    public  boolean  DeleteGroupChatMember(String nameGroup, String userName) throws SQLException, ClassNotFoundException
    {
        sqlConnection.OpenConnection();
        String sql ="Delete from GroupMember where NameGroup=? and Member =?";
        PreparedStatement ps = sqlConnection.GetConnection().prepareStatement(sql);
        ps.setString(1, nameGroup);
        ps.setString(2, userName);
        if(ps.executeUpdate()==1)
        {
            
        }
        return false;
        
    }
    
    public  void LoadGroupChat(List<GroupChat> lsGroupChat)
    {
        try {
            sqlConnection.OpenConnection();
            String sql = "Select *from GroupMember";
            PreparedStatement preparedStatement = sqlConnection.GetConnection().prepareStatement(sql);
            ResultSet rs = preparedStatement.executeQuery();
            String tempGroupName = "";
            while (rs.next()) {
                String groupName = rs.getString("NameGroup");
                if(!groupName.equals(tempGroupName)&& groupName!=null)
                {
                    tempGroupName = groupName;
                    lsGroupChat.add(new GroupChat(groupName, rs.getString("Member")));
                }
                else
                {
                    if(rs.getString("Member")!=null)
                        lsGroupChat.get(lsGroupChat.size()-1).lsClientName.add(rs.getString("Member"));
                        
                }
                
            }
            sqlConnection.CloseConnection();
        } catch (Exception e) {
            Logger.getLogger(Data.class.getName()).log(Level.SEVERE, null, e);
        }
    }
   
}
    
   
