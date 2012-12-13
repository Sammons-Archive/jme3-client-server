/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import mygame.interfaces.GeneralMessage;

/**
 *
 * @author Ben
 */
public class Player {
    
    private PlayerController movement;
    private DefaultOttoGenerator playerMaker;
    private Node playerNode;
    private GeneralMessage message = new GeneralMessage();

    public Player(Node rootNode, AssetManager assetManager, BulletAppState bullet, InputManager inputManager, String name) {
        playerMaker = new DefaultOttoGenerator(rootNode, assetManager, bullet);
        playerNode = playerMaker.generateOtto(name);
        movement = new PlayerController(inputManager,playerNode);
    }
    
    public void move(Vector3f pos, float viewDir,boolean walking, Vector3f walkDir){
        movement.movePlayer(playerNode, walkDir,walking, viewDir, pos, 15f);
    }
    
    public GeneralMessage getMessage() {
        return message;
    }
    
    public void setMessage(GeneralMessage newMessage) {
        message = newMessage;
    }
    
    public Node getPlayerNode() {
        return playerNode;
    }
    
    
}
