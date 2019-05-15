/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import GUI.ServerUi;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thu Tinh
 */
public class ServerThread extends Thread{

    private  Server server;
    private  Socket client;
    private  ObjectInputStream inputStream;
    private  ObjectOutputStream outputStream;
    private int portClient = -1;
    private String username = "";
    private  ServerUi ui ;

    public Socket getClient() {
        return client;
    }

    public void setClient(Socket client) {
        this.client = client;
    }

    public int getPortClient() {
        return portClient;
    }

    public void setPortClient(int portClient) {
        this.portClient = portClient;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    
  
  public ServerThread(Server server, Socket accept) {
       this.server = server;
       this.client = accept;
       portClient = client.getPort();
       ui = server.getUi();
    }

    void OpenThread() throws IOException {
        inputStream = new ObjectInputStream(client.getInputStream());
        outputStream = new ObjectOutputStream(client.getOutputStream());
        outputStream.flush();
    }

  public void send(Message message) throws IOException {
       outputStream.writeObject(message);
       outputStream.flush();
    }
    
  public  void run()
  {
      ui.getTxaMessage().append("Server thread " + portClient + "is running.\n");
      while (true) {          
           try {
            Message msg = (Message)inputStream.readObject();
            server.HandleMessage(portClient, msg);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
      }
     
             
  }
  public  void close() throws IOException
  {
      if(client!=null)
          client.close();
      if(inputStream!=null)
          inputStream.close();
      if(outputStream!=null)
          outputStream.close();
  }

    void send(String signin, String server, String aTrue, String content) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
