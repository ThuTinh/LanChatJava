/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import UI.PrivateChatFrame;
import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.JOptionPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Thu Tinh
 */
public class Download implements  Runnable{
     public ServerSocket server;
    public Socket socket;
    public int port;
    public String saveTo = "";
    public InputStream In;
    public FileOutputStream Out;
    public PrivateChatFrame chatFrame;
    public  String tenFile;
    
    public Download(String saveTo,String tenFile, PrivateChatFrame chatFrame){
        try {
            server = new ServerSocket(0);
            port = server.getLocalPort();
            this.saveTo = saveTo;
            this.chatFrame = chatFrame;
            this.tenFile = tenFile;
            
        } 
        catch (IOException ex) {
            System.out.println("Exception [Download : Download(...)]");
        }
    }

    @Override
    public void run() {
        try {
            socket = server.accept();
            System.out.println("Download : "+socket.getRemoteSocketAddress());
            
            In = socket.getInputStream();
            Out = new FileOutputStream(saveTo);
            
            byte[] buffer = new byte[1024];
            int count;
            
            while((count = In.read(buffer)) >= 0){
                Out.write(buffer, 0, count);
            }
            
            Out.flush();
            
            StyledDocument doc = chatFrame.getTxtMessageInfo().getStyledDocument();
            SimpleAttributeSet left = new SimpleAttributeSet();
            StyleConstants.setAlignment(left, StyleConstants.ALIGN_CENTER);
            Style style = chatFrame.getTxtMessageInfo().addStyle(null, null);
            StyleConstants.setForeground(style, Color.MAGENTA);
            StyleConstants.setUnderline(left, true);
            String message = tenFile + "\n";
            try{
                int length = doc.getLength();
                doc.insertString(doc.getLength(), message, style);
                doc.setParagraphAttributes(length+1, 1, left, false);
                
               
              if(  JOptionPane.showConfirmDialog(chatFrame, "Mởi file " + tenFile + " ngay bây giờ ?")==0)
              {
                  Desktop desktop = Desktop.getDesktop();
                   File file = new File(saveTo);
                    if(file.exists())
                    desktop.open(file);
              }
    
            }
            catch(Exception e) {  
                JOptionPane.showMessageDialog(chatFrame, "Không thể mở file");
            }           
            if(Out != null) 
                Out.close(); 
            if(In != null) 
                In.close(); 
            if(socket != null) 
                socket.close(); 
        } 
        catch (Exception ex) {
           JOptionPane.showMessageDialog(chatFrame, "Dowload " + ex.toString());
        }
    }
}
