/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Logic;


import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Thu Tinh
 */
public class GroupChat {
    public  String groupName ;
     public List<String> lsClientName;
    public GroupChat(String name, String username)
    {
        groupName = name;
        lsClientName = new ArrayList<String>();
        lsClientName.add(username);
        
    }
    
    public  void RemoveMember(String name)
    {
        for(int i = 0; i<lsClientName.size();i++)
        {
            if(lsClientName.get(i).equals(name))
                lsClientName.remove(i);
        }
    }
 
    
}
