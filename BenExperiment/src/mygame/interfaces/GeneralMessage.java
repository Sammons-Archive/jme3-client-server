/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.interfaces;

import com.jme3.math.Vector3f;
import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import mygame.interfaces.MessageImpl;

/**
 *
 * @author Ben
 */
@Serializable
public class GeneralMessage extends AbstractMessage implements MessageImpl {

    private int clientID = -1;
    private int clientNum = -1;
    private Vector3f pos = new Vector3f();
    private Vector3f walkDir = new Vector3f();
    private float theta = 0;
    private int newestPlayer = -1;
    private int mostRecentDisconnect = -1;
    private int playerExistence[] = new int[10];
    private boolean walking = false;

    public GeneralMessage() {
        //super(reliable);
    }

    public void updateMessagePlayerPosition(Vector3f position) {
        pos.x = position.x;
        pos.y = position.y;
        pos.z = position.z;
    }

    public void updateMessageNumPlayers(int num) {
        this.clientNum = num;
    }

    public void updateMessagePlayerID(int num) {
        this.clientID = num;
    }

    public void interpolate(Vector3f newPos) {
        pos.interpolate(newPos, 0.5f);
    }

    public int getClientID() {
        return clientID;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getWalkDirection() {
        return walkDir;
    }

    public float getViewTheta() {
        return theta;
    }

    public void updateMessageViewTheta(float direction) {
        theta = direction;
    }

    public void updateMessageWalkDirection(Vector3f direction) {
        walkDir = direction;
    }

    public int getNumClients() {
        return clientNum;
    }

    public int newestPlayer() {
        return newestPlayer;
    }

    public int mostRecentDisconnect() {
        return mostRecentDisconnect;
    }

    public int[] getPlayerList() {
        return playerExistence;
    }

    public void updatePlayerList(int[] array) {
        playerExistence = array;
    }

    public void updateMostRecentDisconnect(int i) {
        mostRecentDisconnect = i;
    }

    public void updateNewestPlayer(int i) {
        newestPlayer = i;
    }

    public void updateWalking(boolean walking) {
        this.walking = walking;
    }

    public boolean getWalkState() {
        return walking;
    }
}