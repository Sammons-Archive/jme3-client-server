/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.interfaces;

import com.jme3.network.serializing.Serializable;

/**
 *
 * @author Ben
 */
@Serializable
public class NewConnectionMessage implements NewConnectionImpl{

    private int newPlayerID = -1;
    
    public NewConnectionMessage() {
    }

    public int getNewPlayerID() {
        return newPlayerID;
    }

    public void setNewPlayerID(int ID) {
        newPlayerID = ID;
    }
    
}
