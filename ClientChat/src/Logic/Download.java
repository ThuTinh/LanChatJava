/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import UI.GroupChatFrame;
import UI.PrivateChatFrame;
import java.awt.Color;
import java.awt.Component;
import static java.awt.Component.RIGHT_ALIGNMENT;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
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
public class Download implements Runnable {

    public ServerSocket server;
    public Socket socket;
    public int port;
    public String saveTo = "";
    public InputStream In;
    public FileOutputStream Out;
    public PrivateChatFrame chatFrame;
    public GroupChatFrame groupChatFrame;
    public String tenFile;
    String sender;
    String loai = "file";
    boolean filePrivateChat = true;

    public Download(String sender, String saveTo, String tenFile, String loai, PrivateChatFrame chatFrame) {
        try {
            server = new ServerSocket(0);
            port = server.getLocalPort();
            this.saveTo = saveTo;
            this.chatFrame = chatFrame;
            this.tenFile = tenFile;
            this.loai = loai;
            this.sender = sender;
            filePrivateChat = true;
            groupChatFrame = null;
            
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(chatFrame, "down load khoitao" + ex);
        }
    }

    public Download(String sender, String saveTo, String tenFile, String loai, GroupChatFrame grchatFrame) {
        try {
            filePrivateChat = false;
            server = new ServerSocket(0);
            port = server.getLocalPort();
            this.saveTo = saveTo;
            this.groupChatFrame = grchatFrame;
            chatFrame = null;
            this.tenFile = tenFile;
            this.loai = loai;
            this.sender = sender;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(chatFrame, "down load khoitao" + ex);
        }
    }

    @Override
    public void run() {
        try {
            socket = server.accept();
            System.out.println("Download : " + socket.getRemoteSocketAddress());
            In = socket.getInputStream();
            Out = new FileOutputStream(saveTo);
            byte[] buffer = new byte[1024];
            int count;
            while ((count = In.read(buffer)) >= 0) {
                Out.write(buffer, 0, count);
            }

            Out.flush();
            if (loai.equals("file")) {
                JLabel lbtarget = new JLabel();
                lbtarget.setText(sender + ": " + tenFile);
                lbtarget.setForeground(Color.pink);
                if (filePrivateChat) {
                    chatFrame.getPanelChat().add(lbtarget);
                    chatFrame.revalidate();
                } else {
                    groupChatFrame.getTxtChat().add(lbtarget);
                    groupChatFrame.revalidate();
                }

                lbtarget.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent evt) {
                        Desktop desktop = Desktop.getDesktop();
                        File openfile = new File(saveTo);
                        if (openfile.exists()) {
                            try {
                                desktop.open(openfile);
                            } catch (IOException ex) {
                                Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                });

            } else {
                if (loai.equals("img")) {
                    JLabel lbtarget = new JLabel();
                    ImageIcon icon = new ImageIcon(new ImageIcon(saveTo).getImage().getScaledInstance(100, 100, Image.SCALE_DEFAULT));
                    lbtarget.setSize(100, 100);
                    lbtarget.setIcon(icon);
                    JLabel lb = new JLabel(sender + " đã gởi bạn 1 ảnh ");
                    if (filePrivateChat) {
                        chatFrame.getPanelChat().add(lb);
                        chatFrame.getPanelChat().add(lbtarget);
                        chatFrame.revalidate();
                    }
                    else
                    {
                        groupChatFrame.getTxtChat().add(lb);
                        groupChatFrame.getTxtChat().add(lbtarget);
                        groupChatFrame.revalidate();
                    }

                    lbtarget.addMouseListener(new java.awt.event.MouseAdapter() {
                        public void mouseClicked(java.awt.event.MouseEvent evt) {
                            Desktop desktop = Desktop.getDesktop();
                            File openfile = new File(saveTo);
                            if (openfile.exists()) {
                                try {
                                    desktop.open(openfile);
                                } catch (IOException ex) {
                                    Logger.getLogger(Download.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            }
                        }
                    });

                }
            }
//            StyledDocument doc = chatFrame.getTxtMessageInfo().getStyledDocument();
//            SimpleAttributeSet left = new SimpleAttributeSet();
//            StyleConstants.setAlignment(left, StyleConstants.ALIGN_CENTER);
//            Style style = chatFrame.getTxtMessageInfo().addStyle(null, null);
//            StyleConstants.setForeground(style, Color.MAGENTA);
//            StyleConstants.setUnderline(left, true);
//            String message = tenFile + "\n";
//            try {
//                int length = doc.getLength();
//                doc.insertString(doc.getLength(), message, style);
//                doc.setParagraphAttributes(length + 1, 1, left, false);
//
//                if (JOptionPane.showConfirmDialog(chatFrame, "Mởi file " + tenFile + " ngay bây giờ ?", "Thông báo", JOptionPane.YES_NO_OPTION) == 0) {
//                    Desktop desktop = Desktop.getDesktop();
//                    File file = new File(saveTo);
//                    if (file.exists()) {
//                        desktop.open(file);
//                    }
//                }
//
//            } catch (Exception e) {
//                JOptionPane.showMessageDialog(chatFrame, "Không thể mở file");
//            }
            if (Out != null) {
                Out.close();
            }
            if (In != null) {
                In.close();
            }
            if (socket != null) {
                socket.close();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(chatFrame, "Dowload " + ex.toString());
        }
    }
}
