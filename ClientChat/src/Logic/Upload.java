/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import UI.PrivateChatFrame;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.Socket;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 *
 * @author Thu Tinh
 */
public class Upload implements Runnable{
     public String addr;
    public int port;
    public Socket socket;
    public FileInputStream In;
    public OutputStream Out;
    public File file;
    public PrivateChatFrame chatFrame;
    
    public Upload(String addr, int port, File filepath, PrivateChatFrame chatFrame){
        try {
            file = filepath;
            this.chatFrame = chatFrame;
            socket = new Socket(addr, port);
            Out = socket.getOutputStream();
            In = new FileInputStream(filepath);
        } 
        catch (Exception ex) {
            System.out.println("Exception when upload: " + ex);
        }
    }
    
    @Override
    public void run() {
        try {       
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
            String message = file.getName() + " upload completed!" + "\n";
            chatFrame.gettxtFileName().setText("");
            try{
                int length = doc.getLength();
                doc.insertString(doc.getLength(), message, style);
                doc.setParagraphAttributes(length+1, 1, left, false);
            }
            catch(Exception e) { 
                System.out.println(e);
            }
            
            if(In != null)
                In.close(); 
            if(Out != null)
                Out.close(); 
            if(socket != null)
                socket.close(); 
        }
        catch (Exception ex) {
            System.out.println("Exception while uploading: " + ex);
            ex.printStackTrace();
        }
    }
    
}
