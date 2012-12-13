/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.interfaces;

import com.jme3.math.Vector3f;

/**
 *
 * @author Ben
 */
public interface MessageImpl {
    

    public Vector3f getPos();
    public int getClientID();
    public void updateMessagePlayerPosition(Vector3f position);
    public void updateMessageViewTheta(float direction);
    public void updateMessageWalkDirection(Vector3f direction);
    public void updateMessageNumPlayers(int num);
    public void interpolate(Vector3f newPos);
    public Vector3f getWalkDirection();
    public float getViewTheta();
    public int getNumClients();
    public int newestPlayer();
    public int mostRecentDisconnect();
    public void updateMostRecentDisconnect(int i);
    public void updateNewestPlayer(int i);
    public int[] getPlayerList();
    public void updatePlayerList(int array[]);
    public void updateWalking(boolean walking);
    public boolean getWalkState();
        
    
}
