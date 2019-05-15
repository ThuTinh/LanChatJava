/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import UI.GroupChatFrame;
import UI.HistoryFrame;
import UI.LoignFrame;
import UI.MainFrame;
import UI.PrivateChatFrame;
import java.awt.Color;
import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Thu Tinh
 */
public class SocketClient implements Runnable{
    public  Socket socket;
    public  LoignFrame loignFrame;
    public  MainFrame mainFrame;
    public  HistoryFrame historyFrame;
    public  PrivateChatFrame privateChatFrames[];
    public  GroupChatFrame groupChatFrames[];
    public  ObjectInputStream inputStream;
    public  ObjectOutputStream outputStream;
    public  ArrayList<HistoryFrame> lsHistoryFrame;
    public  int countChatFrame = 0;
    public  int countGroupFrame = 0;
    int number = -1;
    int test = 0;
    
    public  SocketClient(LoignFrame lf) throws IOException
    {
        if(lf.serverIP!="")
        {
            try {
            loignFrame = lf;
            socket = new Socket(loignFrame.serverIP,13000);
            outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.flush();
            inputStream = new ObjectInputStream(socket.getInputStream());
            privateChatFrames =new PrivateChatFrame[50];
            groupChatFrames = new GroupChatFrame[30];
            lsHistoryFrame = new ArrayList<HistoryFrame>();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Địa chỉ Ip không hợp lệ");
            }
           
            
        }
        else
            JOptionPane.showMessageDialog(null, "Địa chỉ Ip không hợp lệ");
      
    }
    public  void send(Message msg)
    {
        try {
            outputStream.writeObject(msg);
           // JOptionPane.showMessageDialog(mainFrame,"send"+ msg);
            outputStream.flush();
            
        } catch (Exception e) {
          //  JOptionPane.showMessageDialog(mainFrame,"Lỗi send"+ e.toString());
        }
    }
    
     public int FindHistoryFrame(String sender, String receipient){
      for (int i = 0; i < lsHistoryFrame.size(); i++){
          if (lsHistoryFrame.get(i).getGroupName().equals("")){
            if ((lsHistoryFrame.get(i).getUser().equals(sender) && lsHistoryFrame.get(i).getTaget().equals(receipient) || (lsHistoryFrame.get(i).getUser().equals(receipient) && lsHistoryFrame.get(i).getTaget().equals(sender))))
             return i; 
         }
      }
        return -1;
    }
    public  void closeThread(Thread t)
    {
        t = null;
    }
public  int FindChatFrame(String target)
{
    for(int i = 0; i<=countChatFrame; i++)
    {
        if(privateChatFrames[i]!= null)
        {
            if(privateChatFrames[i].target.equals(target));
            return i;
        }
    }
    return -1;
        
}
 public void SetUserFrame(MainFrame mainFrame){
        this.mainFrame = mainFrame;
    }
 
 public  int FindGroupFame(String name)
 {
     for(int i =0; i<countGroupFrame;i++)
     {
         if(groupChatFrames[i].nameGroup.equals(name))
         {
             return i;
             
         }
        
     }
      return -1;
 }
    @Override
    public void run() {
        boolean keepRunning = true;
        while (keepRunning) {
            try {
                Message msg = (Message)inputStream.readObject();
                switch(msg.type)
                {
                    case "connected":
                    //JOptionPane.showMessageDialog(null, "Connect to server succeed!");
                         
                    loignFrame.getTxfServerIP().setEnabled(false);
                    loignFrame.getBtnConnect().setEnabled(false);
                    loignFrame.getTxfUsername().requestFocus();
                    loignFrame.getTxfUsername().setText("");
                        break;
                    case "signup":
                        if (msg.content.equalsIgnoreCase("true")) {
                            JOptionPane.showMessageDialog(null,"Sign up successfull");
                            loignFrame.getTxfUsername().setText("");
                            loignFrame.getTxtPassword().setText("");
                            
                            
                        }
                    else
                        {
                            JOptionPane.showMessageDialog(null, "Account already exists");
                        }
                    break;
                   case "signout":
                   {
                       for(int i =0;i<mainFrame.model.getSize();i++)
                       {
                           if(msg.content.equals(mainFrame.model.get(i)))
                               mainFrame.model.remove(i);
                       }
                       
                   }
                   break;
                    case "signin":
                    {
                        if(msg.content.equalsIgnoreCase("True"))
                        {
                            JOptionPane.showMessageDialog(null, "Đăng nhập thành công!!");
                            loignFrame.setVisible(false);
                            mainFrame = new MainFrame(this,msg.recipient, loignFrame.clientThread );
                            mainFrame.setVisible(true);
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Đăng nhập thất bại!!");
                        }
                        break;
                    }
                    case "newuser":
                    {
//                        String user = msg.content;
//                        mainFrame.AddUser(user);
                        if(!msg.content.equals(mainFrame.username))
                        {
                            boolean exists = false;
                            for(int i = 0;i<mainFrame.model.getSize();i++)
                            {
                                if(mainFrame.model.getElementAt(i).equals(msg.content))
                                {
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists)
                            {
                                mainFrame.AddUser(msg.content);
                                for(int i = 0;i<countGroupFrame;i++)
                                {
                                    int flag = 0;
                                    for(int j = 0;j<groupChatFrames[i].onlineModel.getSize();j++)
                                    {
                                        if(groupChatFrames[i].onlineModel.getElementAt(j).equals(msg.content))
                                            flag = 1;
                                    }
                                    if(flag==0)
                                    {
                                        
                                        groupChatFrames[i].onlineModel.addElement(msg.content);
                                        //JOptionPane.showMessageDialog(null, "Them online");
                                    }
                                }
                                for(int i = 0; i<countChatFrame;i++)
                                {
                                     this.send(new Message("GroupChatGetUserList", mainFrame.username, groupChatFrames[i].nameGroup, "Server"));
                                }
                            }
                        }
                        
                    }
                    break;
                    case "sendnewuser":
                    {
//                        String name = msg.content;
//                        mainFrame.AddUser(name);
                         if(!msg.content.equals(mainFrame.username))
                        {
                            boolean exists = false;
                            for(int i = 0;i<mainFrame.model.getSize();i++)
                            {
                                if(mainFrame.model.getElementAt(i).equals(msg.content))
                                {
                                    exists = true;
                                    break;
                                }
                            }
                            if(!exists)
                            {
                                mainFrame.AddUser(msg.content);
                                for(int i = 0;i<countGroupFrame;i++)
                                {
                                    int flag = 0;
                                    for(int j = 0;j<groupChatFrames[i].memberModel.getSize();j++)
                                    {
                                        if(groupChatFrames[i].memberModel.getElementAt(j).equals(msg.content))
                                            flag = 1;
                                    }
                                    if(flag==0)
                                    {
                                        
                                        groupChatFrames[i].onlineModel.addElement(msg.content);
                                       // JOptionPane.showMessageDialog(null, "Them online");
                                    }
                                }
                                for(int i = 0; i<countChatFrame;i++)
                                {
                                     this.send(new Message("GroupChatGetUserList", mainFrame.username, groupChatFrames[i].nameGroup, "Server"));
                                }
                            }
                        }
                    }
                    break;
                    case "privatechat":
                    {
                    
                            if(this.privateChatFrames[0]==null || this.FindChatFrame(msg.sender)==-1)
                            {
                                privateChatFrames[countChatFrame]= new PrivateChatFrame(this, msg.sender, msg.recipient);
                                privateChatFrames[countChatFrame].setVisible(true);
                                countChatFrame++;
                            }
                            else
                            {
                                privateChatFrames[FindChatFrame(msg.sender)].setVisible(true);
                            }
                                
                           if( privateChatFrames[FindChatFrame(msg.sender)].target.equals(msg.sender))
                           {
                              // privateChatFrames[FindChatFrame(msg.sender)].getTxtMessage().setText(msg.content);
                                StyledDocument doc = privateChatFrames[FindChatFrame(msg.sender)].getTxtMessageInfo().getStyledDocument();
                                SimpleAttributeSet left = new SimpleAttributeSet();
                                StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
                                Style style = privateChatFrames[FindChatFrame(msg.sender)].getTxtMessageInfo().addStyle(null, null);
                                StyleConstants.setForeground(style, Color.black);
                               // String message = msg.content;
                                //String arr [] = message.split("/");
                                int length = doc.getLength();
                               // privateChatFrames[FindChatFrame(msg.sender)].getTxtMessage().setText("");
                                //doc.insertString(length, arr[1]+"\n", style);
                                doc.insertString(length, msg.content +"\n", style);
                                doc.setParagraphAttributes(length+1, 1, left, false);
                           
                           }                
                        
                    }
                    break;
                  case "showHistory":
                  {
                      
                      if(FindHistoryFrame(msg.sender, msg.recipient)==-1)
                      {
                          HistoryFrame historyFrame ;
                          if(msg.sender.equals(mainFrame.username))
                          {
                              historyFrame = new HistoryFrame(msg.sender, msg.recipient);
                              
                          }
                          else
                              historyFrame = new HistoryFrame(msg.recipient, msg.sender);
                          lsHistoryFrame.add(historyFrame);
                      }
                           lsHistoryFrame.get(FindHistoryFrame(msg.sender, msg.recipient)).setVisible(true);
                    lsHistoryFrame.get(FindHistoryFrame(msg.sender, msg.recipient)).AddMessage(msg.sender, msg.recipient, msg.content);
                  }
                  break;
                 case "CreateGroupChat":
                 {
                     try {
                         if(!msg.content.equalsIgnoreCase("false"))
                     {
                         if(FindGroupFame(msg.content)==-1)
                         {
                            // JOptionPane.showMessageDialog(null, "Tên group là: " + msg);
                             groupChatFrames[countGroupFrame] = new GroupChatFrame(this, mainFrame.username, msg.content, mainFrame);
                           
                            // mainFrame.AddGroup(msg.content);
                             mainFrame.groupListModel.addElement(msg.content);
                            // mainFrame.groupListModel.remove(mainFrame.groupListModel.getSize()-1);
                             
                             countGroupFrame++;
                         }
                     }
                   else
                     JOptionPane.showMessageDialog(null, "Group chat name already exists!");
                         
                     } catch (Exception e) {
                         JOptionPane.showMessageDialog(null, "CreateGroupErro: "+ e.toString());
                     }
                     
                 }
                 break;
                 case "ListGroup":
                 {
                     mainFrame.AddGroup(msg.content);
                     groupChatFrames[countGroupFrame] = new GroupChatFrame(this, mainFrame.username, msg.content, mainFrame);
                    countGroupFrame++; 
                 }
                 break;
                 case "GroupChatGetUserList":
                 {
                     String[] userlist = msg.content.split(",,");
                     int groupId = FindGroupFame(msg.recipient);
                  
                   // JOptionPane.showMessageDialog(null,"check " +  groupChatFrames[groupId].onlineModel.getElementAt(0));
                     for(int i = 0;i<userlist.length; i++)
                     {
                         int flag = 0;
                         for(int j = 0; j<groupChatFrames[groupId].memberModel.getSize();j++)
                         {
                            if(groupChatFrames[groupId].memberModel.getElementAt(j).equals(userlist[i]))
                            {
                                flag=1;
                                break;
                           }
                         }
                         if(flag==0)
                             groupChatFrames[groupId].memberModel.addElement(userlist[i]);
                         for(int j = 0;j<groupChatFrames[groupId].onlineModel.getSize();j++)
                         {
                             String online  = groupChatFrames[groupId].onlineModel.getElementAt(j).toString();
                             String tem = userlist[i];
                            //  JOptionPane.showMessageDialog(null,"la sao online " +online + " tem :" +tem  );
                             if(online.equals(tem));
                             { 

                            //   JOptionPane.showMessageDialog(null,"remove  " +  userlist[j]);
                                 groupChatFrames[groupId].onlineModel.removeElementAt(j);
                             }
                           
                        }
                     }
                 }
                 break;
                 
                 case "AddMemberGroup":
                 {
                     int groupId = FindGroupFame(msg.recipient);
                     int flag = 0;
                     for(int j = 0; j<groupChatFrames[groupId].memberModel.getSize();j++)
                     {
                         if(groupChatFrames[groupId].memberModel.getElementAt(j).equals(msg.content))
                             flag = 1;
                     }
                     if(flag==0)
                     {
                         groupChatFrames[groupId].memberModel.addElement(msg.content);
                         
                     }
                     for(int i = 0;i<groupChatFrames[groupId].onlineModel.getSize();i++)
                     {
                         if(groupChatFrames[groupId].onlineModel.getElementAt(i).equals(msg.content))
                             groupChatFrames[groupId].onlineModel.removeElementAt(i);
                     }
                     
                 }
                 break;
                 case "GroupChat":
                 {
                      if(this.groupChatFrames[0]==null || this.FindGroupFame(msg.recipient)==-1)
                            {
                                groupChatFrames[countGroupFrame]= new GroupChatFrame(this, mainFrame.username, msg.recipient,mainFrame);
                                this.send(new Message("GroupChatGetUserList", mainFrame.username, msg.recipient, "Server"));
                                groupChatFrames[countGroupFrame].setVisible(true);
                                countGroupFrame++;
                            }
                            else
                            {
                                groupChatFrames[FindGroupFame(msg.recipient)].setVisible(true);
                            }
                     
                     
                       int groupID = FindGroupFame(msg.recipient);
                    StyledDocument doc = groupChatFrames[groupID].getTxtMessageInfo().getStyledDocument();
                    SimpleAttributeSet right = new SimpleAttributeSet();
                     StyleConstants.setAlignment(right, StyleConstants.ALIGN_LEFT);
                        Style style = groupChatFrames[groupID].getTxtMessageInfo().addStyle(null, null);
                        StyleConstants.setForeground(style, Color.RED);
                        try
                        {
                            int length = doc.getLength();
                            String message = msg.content+"\n" ;
                            doc.insertString(doc.getLength(), message, style);
                            doc.setParagraphAttributes(length+1, 1, right, false);
                        }
                        catch(Exception e) { System.out.println(e);
                        }
                 }
                 break;
                 case "GroupLeave":
                 {
                     if(msg.content.equals(mainFrame.username))
                      {
                          RemoveGroup(msg.recipient);
                          for(int i = 0;i<mainFrame.groupListModel.getSize();i++)
                          {
                              if(mainFrame.groupListModel.getElementAt(i).equals(msg.recipient))
                                  mainFrame.groupListModel.removeElementAt(i);
                          }
                      }
                     else
                     {
                          int groupID = FindGroupFame(msg.recipient);
                        groupChatFrames[groupID].MoveUserFromUserToAddModel(msg.content);
                        StyledDocument doc = groupChatFrames[groupID].getTxtMessageInfo().getStyledDocument();
                        SimpleAttributeSet right = new SimpleAttributeSet();
                        StyleConstants.setAlignment(right, StyleConstants.ALIGN_CENTER);
                        Style style = groupChatFrames[groupID].getTxtMessageInfo().addStyle(null, null);
                        StyleConstants.setForeground(style, Color.RED);
                        try
                        {
                            int length = doc.getLength();
                            String message = msg.content + " has left this group!\n";
                            doc.insertString(doc.getLength(), message, style);
                            doc.setParagraphAttributes(length+1, 1, right, false);
                        }
                        catch(Exception e) { System.out.println(e);
                        }
                     }
                     
                 }
                 break;
             case "groupchat-create":
              {
                  mainFrame.groupListModel.addElement(msg.content);
                  groupChatFrames[countGroupFrame] = new GroupChatFrame(this,msg.recipient , msg.content, mainFrame);
                 for(int i = 0; i<mainFrame.model.getSize(); i++)
                 {
                       groupChatFrames[countChatFrame].onlineModel.addElement(mainFrame.model.getElementAt(i));
                 }
                 this.send(new Message("GroupChatGetUserList", mainFrame.username, groupChatFrames[countGroupFrame].nameGroup, "Server"));
                  countGroupFrame++;
                 
              }
             break;
              case "GroupChatHistory":
              {
                  if(FindHistoryGroupFrame(msg.recipient)==-1)
                  {
                      HistoryFrame historyFrame  = new HistoryFrame(msg.sender, msg.recipient, "");
                      lsHistoryFrame.add(historyFrame);
                      
                      
                  }
               
                  lsHistoryFrame.get(FindHistoryGroupFrame(msg.recipient)).setVisible(true);
                  lsHistoryFrame.get(FindHistoryGroupFrame(msg.recipient)).AddGroupMessage(msg.sender, msg.recipient, msg.content);
                  
              }
              break;
              case "UploadReq":
              {
                  try {
                       if(privateChatFrames[0]==null ||this.FindChatFrame(msg.sender)==-1)
                  {
                      for(int i = 0;i< mainFrame.model.getSize(); i++)
                      {
                          if(mainFrame.model.getElementAt(i).equals(msg.sender))
                          {
                              privateChatFrames[countChatFrame] = new PrivateChatFrame(this, msg.sender, msg.recipient); 
                              privateChatFrames[countChatFrame].setVisible(true);
                              countChatFrame ++;
                              number = FindChatFrame(msg.sender);
                              break;
                              
                                      
                          }
                      }
                    
                  }
                  else
                  {
                      if(FindChatFrame(msg.sender)!=-1)
                      {
                          privateChatFrames[this.FindChatFrame(msg.sender)].setVisible(true);
                          number = FindChatFrame(msg.sender);
                          
                      }
                  }
                  int chatFrameNumber = FindChatFrame(msg.sender);
                  if(JOptionPane.showConfirmDialog(privateChatFrames[chatFrameNumber], ("Accept" + msg.content+ "from "+ msg.sender + "?"))==0)               
                  {
                      JFileChooser jf = new JFileChooser();
                      jf.setSelectedFile(new File(msg.content));
                      int returnVal = jf.showSaveDialog(privateChatFrames[chatFrameNumber]);
                      String saveTo  = jf.getSelectedFile().getPath(); 
                      
                          if(saveTo!= null && returnVal ==JFileChooser.APPROVE_OPTION)
                          {
                              Download dwn = new Download(saveTo, msg.content, privateChatFrames[chatFrameNumber]);
                              Thread t  = new Thread(dwn);
                              t.start();
                            //  this.send(new Message("UploadRes", ""+InetAddress.getLocalHost().getHostAddress(), "" +dwn.port, msg.sender));
                              this.send(new Message("UploadRes", mainFrame.username, ""+ dwn.port, msg.sender));
                              
                          }
                          else
                          {
                              this.send(new Message("UploadRes", mainFrame.username,"No" , msg.sender));
                          }
                      
                              
                  }
                  else
                  {
                      this.send(new Message("UploadRes", mainFrame.username,"No" , msg.sender));
                  }
                  } catch (Exception e) {
                      JOptionPane.showMessageDialog(null,"Lỗi upladReq  client "+e.toString());
                      
                  }
                  
                 
                      
              }
              break;
             case "UploadRes":
             {
                 if(FindChatFrame(msg.sender)!=-1)
                 {
                     number = FindChatFrame(msg.sender);
                     if(number!=-1)
                     {
//                         if(test==1)
//                         {
                             if(!msg.content.equals("No"))
                             {
                                 int port = Integer.parseInt(msg.content);
                                 String addr = msg.sender;
                                 if(privateChatFrames[number]!=null)
                                 {
                                     Upload upload = new Upload(addr, port,privateChatFrames[number].file , privateChatFrames[number]);
                                     Thread t = new Thread(upload);
                                     t.start();
                                 }
                                 else
                                 {
                                     
                                 }
                                     
                             }
                             else
                             {
                                 StyledDocument doc = privateChatFrames[number].getTxtMessageInfo().getStyledDocument();
                                SimpleAttributeSet left = new SimpleAttributeSet();
                                StyleConstants.setAlignment(left, StyleConstants.ALIGN_LEFT);
                                Style style = privateChatFrames[number].getTxtMessageInfo().addStyle(null, null);
                                StyleConstants.setForeground(style, Color.black);
                                String message = msg.sender + " rejected file request!\n";
                                try {
                                    int length = doc.getLength();
                                    doc.insertString(doc.getLength(), message, style);
                                    doc.setParagraphAttributes(length+1, 1, left, false);
                                }
                                catch(Exception e) {
                                    JOptionPane.showMessageDialog(null,"Lỗi upladRes  client "+e.toString());
                                }
                             }
                          //   test = 0;
                         }
//                     }
//                     else
//                         test = 1;
                 }
                 
             }
             break;
                      
                     
    
                }
  
            } catch (Exception e) {
               // JOptionPane.showMessageDialog(mainFrame, "hix " + e.toString());
                
            }
            
        }
        
    }

   
    private void RemoveGroup(String name) {
          int flag = -1;
        for (int i = 0; i < countGroupFrame; i++){
            if (groupChatFrames[i].nameGroup.equals(name)){
                flag = i;
                break;
            }
        }
        if (flag != -1){
            countGroupFrame -= 1;
            if (countGroupFrame > 0){
                for (int i = flag; i < countGroupFrame; i++){
                    if (groupChatFrames[i + 1] != null){
                        groupChatFrames[i] = groupChatFrames[i + 1];
                    }
                }
            }
            else
               groupChatFrames[flag] = null;
        }
    }
    
     public  int FindHistoryGroupFrame(String groupName)
    {
        for(int i = 0; i< lsHistoryFrame.size();i++)
        {
            if(!lsHistoryFrame.get(i).getGroupName().equals(""))
            {
                if(lsHistoryFrame.get(i).getGroupName().equals(groupName))
                {
                    return i;
                }
                    
            }
        }
    
        return -1;
      }
}

