/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import Logic.Message;
import Logic.SocketClient;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import sun.font.CreatedFontTracker;

/**
 *
 * @author Thu Tinh
 */
public class MainFrame extends javax.swing.JFrame {

    public SocketClient client;
    public String username;
    public Thread clientThread;
    public DefaultListModel model, searchListModel, groupListModel;

    /**
     * Creates new form MainFrame
     */
    public MainFrame(SocketClient socketClient, String recipient, Thread clientThread) {
        initComponents();
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("iconhome.png")));
        super.setResizable(false);
        client = socketClient;
        username = recipient;
        this.clientThread = clientThread;
        client.SetUserFrame(this);
        lbName.setText(recipient);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Bạn muốn đăng xuất?", "Thông báo", JOptionPane.YES_NO_OPTION);
                if (option == 0) {
                    client.send(new Message("signout", username, "true", "server"));
                    clientThread.stop();
                    System.exit(0);
                }

            }
        });

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        lbName = new javax.swing.JLabel();
        jTabbedPane4 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        btnCreateGroup = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        listGroup = new javax.swing.JList()
        ;
        jPanel2 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        listUser = new javax.swing.JList();
        btnDangXuat = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jPanel1.setBackground(new java.awt.Color(214, 90, 49));

        lbName.setFont(new java.awt.Font("Tekton Pro Ext", 1, 24)); // NOI18N
        lbName.setText("Name");
        lbName.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lbNameMouseClicked(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(34, 40, 49));

        btnCreateGroup.setBackground(new java.awt.Color(34, 40, 49));
        btnCreateGroup.setForeground(new java.awt.Color(255, 255, 255));
        btnCreateGroup.setText("Create new group");
        btnCreateGroup.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnCreateGroup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCreateGroupActionPerformed(evt);
            }
        });

        listGroup.setModel((groupListModel = new DefaultListModel()));
        listGroup.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                listGroupMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(listGroup);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 183, Short.MAX_VALUE)
                        .addComponent(btnCreateGroup)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnCreateGroup)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Group", jPanel3);

        jPanel2.setBackground(new java.awt.Color(34, 40, 49));

        listUser.setModel((model = new DefaultListModel()));
        listUser.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                lisUsersMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(listUser);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane4.addTab("Users", jPanel2);

        btnDangXuat.setBackground(new java.awt.Color(214, 90, 49));
        btnDangXuat.setText("Đăng xuất");
        btnDangXuat.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane4)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 167, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDangXuat))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbName, javax.swing.GroupLayout.PREFERRED_SIZE, 56, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDangXuat))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane4))
        );

        jTabbedPane4.getAccessibleContext().setAccessibleName("");

        getContentPane().add(jPanel1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        // TODO add your handling code here:
        int option = JOptionPane.showConfirmDialog(null, "Bạn muốn đăng xuất?","Thông báo", JOptionPane.YES_NO_OPTION);
        if (option == 0) {
            client.send(new Message("signout", username, "true", "server"));
            clientThread.stop();
            System.exit(0);
        }
    }//GEN-LAST:event_btnDangXuatActionPerformed

    private void listGroupMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_listGroupMouseClicked
        // TODO add your handling code here:
        try {
            listGroup = (JList) evt.getSource();
            if (evt.getClickCount() == 2) {
                int index = listGroup.locationToIndex(evt.getPoint());
                String groupName = listGroup.getSelectedValue().toString();

                if (client.FindGroupFame(groupName) != -1) {
                    //                 for(int i = 0; i<model.getSize();i++)
                    //                 {
                    //                     client.groupChatFrames[client.FindGroupFame(groupName)].onlineModel.addElement(model.getElementAt(i));
                    //
                    //                 }

                    client.send(new Message("GroupChatGetUserList", username, groupName, "Server"));
                    client.groupChatFrames[client.FindGroupFame(groupName)].setVisible(true);

                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "LoadNhomErro" + e.toString());
        }
    }//GEN-LAST:event_listGroupMouseClicked

    private void btnCreateGroupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateGroupActionPerformed
        // TODO add your handling code here:
        CreateGroupFrame createGroupFrame = new CreateGroupFrame(client, username);
        createGroupFrame.setVisible(true);
    }//GEN-LAST:event_btnCreateGroupActionPerformed

    private void lbNameMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_lbNameMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_lbNameMouseClicked

    /**
     * @param args the command line arguments
     */
    public JList getListGroup() {
        return listGroup;
    }

    public JList getListUser() {
        return listUser;
    }

    public void AddUser(String name) {
        model.addElement(name);
        // JOptionPane.showMessageDialog(rootPane,"them user"+ name);
    }

    public void AddGroup(String nameGroup) {
        groupListModel.addElement(nameGroup);
    }

    public void lisUsersMouseClicked(java.awt.event.MouseEvent evt) {
        listUser = (JList) evt.getSource();
        String target = listUser.getSelectedValue().toString();
        if (evt.getClickCount() == 2) {
         //  int index = listUser.locationToIndex(evt.getPoint());

            if (client.privateChatFrames[0] == null || client.FindChatFrame(target) == -1) {
                client.privateChatFrames[client.countChatFrame] = new PrivateChatFrame(client, target, username);
                client.privateChatFrames[client.countChatFrame].setVisible(true);
                client.countChatFrame++;
                // client.send(new Message(target, target, target, target));
            }
        } else if (client.FindChatFrame(target) != -1) {
            client.privateChatFrames[client.FindChatFrame(target)].setVisible(true);
        }

    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCreateGroup;
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTabbedPane jTabbedPane4;
    private javax.swing.JLabel lbName;
    public javax.swing.JList listGroup;
    public javax.swing.JList listUser;
    // End of variables declaration//GEN-END:variables
}
