/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.ServerUi;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import sun.security.pkcs.PKCS9Attribute;

/**
 *
 * @author Thu Tinh
 */
public class Server implements Runnable {

    private ServerThread clients[];
    private ServerSocket server;
    private final int port = 13000;
    private Thread thread = null;
    private int count;
    private ServerUi ui;
    Data db;
    private List<GroupChat> lsGroupChat;

    public ServerUi getUi() {
        return ui;
    }

    public void setUi(ServerUi ui) {
        this.ui = ui;
    }

    public Server(ServerUi ui) throws IOException {
        this.ui = ui;
        clients = new ServerThread[50];
        server = new ServerSocket(port);
        count = 0;
        db = new Data();
        lsGroupChat = new ArrayList<GroupChat>();
        db.LoadGroupChat(lsGroupChat);
        ui.getTxaMessage().append("Server started : " + InetAddress.getLocalHost() + " port: " + server.getLocalPort() + "\n");
        start();

    }

    @Override
    public void run() {
        while (thread != null) {
            try {
                ui.getTxaMessage().append("waiting for client...\n");
                AddThread(server.accept());
            } catch (Exception e) {
            }

        }
    }

    private void start() {
        if (thread == null) {
            thread = new Thread(this);
        }
        thread.start();
    }

    private void AddThread(Socket accept) {
        if (count < clients.length) {
            clients[count] = new ServerThread(this, accept);
            ui.getTxaMessage().append("Accept!\n");
            try {
                clients[count].OpenThread();
                clients[count].start();
                String clientPort = "Your port: " + clients[count].getPortClient();
                clients[count].send(new Message("connected", "Server", "True", clientPort));
                count++;
            } catch (Exception e) {
            }
        }

    }

    void HandleMessage(int portClient, Message msg) throws IOException, SQLException, ClassNotFoundException {

        switch (msg.getType()) {

            case "signup":
                SignUp(portClient, msg);
                break;
            case "signin":

                SignIn(portClient, msg);
                break;
            case "signout":
                SignOut(portClient, msg);
            case "privatechat":

                PrivateChat(portClient, msg);
                break;
            case "showHistory":
                ShowHistory(portClient, msg);
                break;

            case "CreateGroup":
                CreateGroup(portClient, msg);
                break;
            case "AddMemberGroup":
                AddMemberGroup(portClient, msg);
                break;
            case "GroupChat":
                GroupChat(portClient, msg);
                break;
            case "GroupChatGetUserList":
                GroupChatGetUserList(portClient, msg);
                break;
            case "groupchat-leave":
                GroupLeave(portClient, msg);
                break;
            case "GroupChatHistory":
                GroupChatHistory(portClient, msg);
                break;
            case "UploadReq":
                UploadReq(portClient, msg);
                break;
            case "UploadRes":
                UploadRes(portClient, msg);

        }
    }

    private void SignUp(int portClient, Message msg) throws IOException, SQLException {
        if (!msg.getSender().equals("")) {
            try {
                db.AddUser(msg.getSender(), msg.getContent());
            } catch (Exception e) {
                // JOptionPane.showMessageDialog(null,"Lỗi dữ liệu");
            }

            clients[FindClient(portClient)].setUsername(msg.getSender());
            clients[FindClient(portClient)].send(new Message("signup", "Server", "True", msg.getSender()));
            // clients[FindClient(portClient)].send(new Message("signin","Server", "True", msg.getSender()));
            SignIn(portClient, msg);

        }
    }

    private int FindClient(int port) {
        for (int i = 0; i < count; i++) {
            if (clients[i].getPortClient() == port) {
                return i;
            }
        }
        return -1;
    }

    private int FindCient(String name) {
        for (int i = 0; i < count; i++) {
            if (clients[i].getUsername().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public ServerThread FindUServerThread(String username) {
        for (int i = 0; i < count; i++) {
            if (clients[i].getUsername().equals(username)) {
                return clients[i];
            }
        }
        return null;
    }

    private void SignIn(int portClient, Message msg) throws SQLException {
        try {
            if (db.UserExists(msg.getSender()) == true) {
                clients[FindClient(portClient)].setUsername(msg.getSender());
                clients[FindClient(portClient)].send(new Message("signin", "Server", "True", msg.getSender()));
                SendListUser(portClient);
                SendNewUser(portClient, msg.getSender());
                for (int i = 0; i < lsGroupChat.size(); i++) {
                    for (int j = 0; j < lsGroupChat.get(i).lsClientName.size(); j++) {
                        if (lsGroupChat.get(i).lsClientName.get(j).equals(msg.getSender())) {
                            clients[FindCient(msg.getSender())].send(new Message("ListGroup", "Server", lsGroupChat.get(i).groupName, msg.getSender()));
                        }

                    }

                }
            } else {
                clients[FindClient(portClient)].send(new Message("signin", "Server", "false", msg.getSender()));
            }
        } catch (Exception e) {
            // JOptionPane.showMessageDialog(ui, e.toString()+ "lỗi j đây");
        }

    }

    public void SendListUser(int port) throws IOException {
        int j = FindClient(port);
        for (int i = 0; i < count; i++) {
            if (i != j) {
                clients[j].send(new Message("newuser", "server", clients[i].getUsername(), clients[j].getUsername()));

            }
        }
    }

    public void SendNewUser(int port, String name) throws IOException {
        int j = FindClient(port);
        for (int i = 0; i < count; i++) {

            if (i != j) {
                clients[i].send(new Message("newuser", "server", name, clients[j].getUsername()));

            }
        }

    }

    private void PrivateChat(int portClient, Message msg) throws IOException, SQLException, ClassNotFoundException {
        int i = FindCient(msg.getRecipient());
        Timestamp now = Timestamp.valueOf(LocalDateTime.now());
        db.AddPrivateChat(msg.getSender(), msg.getRecipient(), now, msg.getContent());
        //msg.setContent(msg.getContent());
        clients[i].send(msg);

    }

    private void ShowHistory(int portClient, Message msg) {
        db.SendHistory(msg.getSender(), msg.getContent(), clients[FindCient(msg.getSender())]);
    }

    private void SignOut(int portClient, Message msg) throws IOException {
        Remove(portClient);
        for (int i = 0; i < count; i++) {
            if (clients[i] != null) {
                clients[i].send(new Message("signout", "SERVER", msg.getSender(), "All"));
            }
        }
        clients[portClient].stop();
    }

    private void Remove(int port) {
        int pos = FindClient(port);
        if (pos >= 0) {
            ServerThread toTerminate = clients[pos];
            ui.getTxaMessage().append("\nRemoving client thread " + port + " at " + pos + "\n");
            if (pos < count - 1) {
                for (int i = pos + 1; i < count; i++) {
                    clients[i - 1] = clients[i];
                }

            }
            count--;
            try {
                toTerminate.close();
            } catch (Exception e) {
                ui.getTxaMessage().append("\nError closing thread: " + e);
            }
        }
    }

    private void CreateGroup(int portClient, Message msg) {
        try {
            if (FindGroupName(msg.getContent()) == -1) {
                GroupChat groupChat = new GroupChat(msg.getContent(), msg.getSender());
                if (db.CreateGroupChat(groupChat)) {
                    lsGroupChat.add(groupChat);
                }
                clients[FindClient(portClient)].send(new Message("CreateGroupChat", "Server", msg.getContent(), msg.getSender()));

            } else {
                clients[FindClient(portClient)].send(new Message("CreateGroupChat", "Server", "False", msg.getSender()));
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "CreateGroupErrobrnserver: " + e.getMessage());
        }

    }

    public int FindGroupName(String name) {

        for (int i = 0; i < lsGroupChat.size(); i++) {
            if (lsGroupChat.get(i) != null) {
                if (lsGroupChat.get(i).groupName.equals(name)) {
                    return i;
                }
            }

        }
        return -1;
    }

    private void AddMemberGroup(int portClient, Message msg) throws SQLException, ClassNotFoundException, IOException {
        if (FindCient(msg.getContent()) != -1) {
            Timestamp t = Timestamp.valueOf(LocalDateTime.now());
            db.AddMemberGroup(msg.getRecipient(), msg.getContent(), msg.getSender(), t);
            int groupID = FindGroupName(msg.getRecipient());
            lsGroupChat.get(groupID).lsClientName.add(msg.getContent());
            clients[FindCient(msg.getContent())].send(new Message("groupchat-create", "Server", msg.getRecipient(), msg.getContent()));
            String userlist = msg.getSender();
            for (int i = 0; i < lsGroupChat.get(groupID).lsClientName.size(); i++) {
                if (lsGroupChat.get(groupID).lsClientName.get(i) != null) {
                    userlist = userlist + ",," + lsGroupChat.get(groupID).lsClientName.get(i);
                    int check_online = FindCient(lsGroupChat.get(groupID).lsClientName.get(i));
                    if (check_online != -1) {
                        clients[FindCient(lsGroupChat.get(groupID).lsClientName.get(i))].send(new Message("AddMemberGroup", "Server", msg.getContent(), msg.getRecipient()));
                        clients[FindCient(lsGroupChat.get(groupID).lsClientName.get(i))].send(new Message("GroupChat", "SERVER", msg.getContent() + " has joined this group!", msg.getRecipient()));
                    }
                }
            }
            clients[FindCient(msg.getContent())].send(msg);

        }

    }

    private void GroupChat(int portClient, Message msg) throws IOException, SQLException {
        try {
            int groupId = FindGroupName(msg.getRecipient());
            Timestamp t = Timestamp.valueOf(LocalDateTime.now());
            db.AddGroupChatHistory(msg.getSender(), msg.getRecipient(), msg.getContent(), t);
            for (int i = 0; i < lsGroupChat.get(groupId).lsClientName.size(); i++) {
                int check_online = FindCient(lsGroupChat.get(groupId).lsClientName.get(i));
                if (check_online != -1 && !lsGroupChat.get(groupId).lsClientName.get(i).equals(msg.getSender())) {
                    clients[FindCient(lsGroupChat.get(groupId).lsClientName.get(i))].send(new Message("GroupChat", msg.getSender(), msg.getSender() + ":" + " " + msg.getContent(), lsGroupChat.get(groupId).groupName));
                }
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Lỗi xử lý dữ liệu");
        }

    }

    private void GroupChatGetUserList(int portClient, Message msg) throws IOException {
        try {
            int index = FindGroupName(msg.getContent());
            String userList = msg.getSender();
            for (int i = 0; i < lsGroupChat.get(index).lsClientName.size(); i++) {
                if (lsGroupChat.get(index).lsClientName.get(i) != null && !lsGroupChat.get(index).lsClientName.get(i).equals(msg.getSender())) {
                    userList = userList + ",," + lsGroupChat.get(index).lsClientName.get(i);
                }
            }
            clients[FindCient(msg.getSender())].send(new Message("GroupChatGetUserList", "Server", userList, msg.getContent()));
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "GoiDanhSAchNhomLoiServer" + e.toString());
        }

    }

    private void GroupLeave(int portClient, Message msg) {
        try {
            if (db.DeleteGroupChatMember(msg.getRecipient(), msg.getSender())) {
                int groupId = FindGroupName(msg.getRecipient());
                lsGroupChat.get(groupId).RemoveMember(msg.getSender());
                clients[FindCient(msg.getSender())].send(new Message("GroupLeave", "server", msg.getSender(), msg.getRecipient()));
                for (int i = 0; i < lsGroupChat.get(groupId).lsClientName.size(); i++) {
                    if (lsGroupChat.get(groupId).lsClientName.get(i) != null) {
                        clients[FindCient(lsGroupChat.get(groupId).lsClientName.get(i))].send(new Message("GroupLeave", "server", msg.getSender(), msg.getRecipient()));
                    }
                }
            } else {
                clients[FindCient(msg.getSender())].send(new Message("GroupLeave", "server", "fail", msg.getRecipient()));
            }

        } catch (Exception e) {
            // JOptionPane.showMessageDialog(ui, "Rời khởi nhóm không thành công" + e.toString());
        }
    }

    private void GroupChatHistory(int portClient, Message msg) {
        try {
            db.SendGroupHistory(msg.getContent(), FindUServerThread(msg.getSender()));

        } catch (Exception e) {
            //JOptionPane.showMessageDialog(ui, "loi nhan history: "+ e.toString() );
        }

    }

    private void UploadReq(int portClient, Message msg) throws IOException {
        try {
            if (msg.getRecipient().equals("All")) {
                clients[FindClient(portClient)].send(new Message("message", "sever", "Uploading to All forbidden", msg.getSender()));
            } else {
                FindUServerThread(msg.getRecipient()).send(new Message("UploadReq", msg.getSender(), msg.getContent(), msg.getRecipient()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ui, "Lỗi upladReq server " + e.toString());
        }
    }

    private void UploadRes(int portClient, Message msg) throws IOException {
        try {
            if (!msg.getContent().equals("No")) {
                if (FindUServerThread(msg.getSender().toString()) != null) {
                    String Ip = FindUServerThread(msg.getSender()).getClient().getInetAddress().getHostAddress();
                    // FindUServerThread(msg.getRecipient()).send(new Message("UploadRes",msg.getSender(),msg.getContent(),msg.getRecipient()));
                    FindUServerThread(msg.getRecipient()).send(new Message("UploadRes", msg.getSender(), msg.getContent() + "-" + Ip, msg.getRecipient()));

                } else {
                }
            } else {
                FindUServerThread(msg.getRecipient()).send(new Message("UploadRes", msg.getSender(), msg.getContent(), msg.getRecipient()));
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(ui, "Lỗi upladRes server " + e.toString());
        }

    }
}
