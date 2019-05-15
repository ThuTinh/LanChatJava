/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;

import java.io.Serializable;

/**
 *
 * @author Thu Tinh
 */
public class Message implements Serializable{

    private String type ;
    private String sender ;
    private String content ;
    private  byte [] avatar;
    private String recipient;

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }
    private  static final long serialVersionUID = 1L;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getAvatar() {
        return avatar;
    }

    public void setAvatar(byte[] avatar) {
        this.avatar = avatar;
    }
    public  Message(String type , String sender ,String content , String recipient  )
    {
        this.content = content;
        this.recipient = recipient;
        this.type = type;
        this.sender = sender;
        this.avatar = null;
    }
    
    public  Message(String type, String sender , byte [] avatar,String recipient )
    {
        this.type = type;
        this.sender = sender;
        this.avatar = avatar;
        this.recipient = recipient;
        this.content = null;
    }
    
    public  String toString()
    {
       return "{type='"+type+"', sender='"+sender+"', content='"+content+"', recipient='"+recipient+"', avatar='"+avatar+"'}";
    }
}
