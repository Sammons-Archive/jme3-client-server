/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Ben
 */
public class DefaultOttoGenerator {

    private Node playerNode;
    private CharacterControl playerControl;
    private Node rootNode;
    private BulletAppState bullet;
    private AssetManager assetManager;

    public DefaultOttoGenerator(Node rootNode, AssetManager assetManager, BulletAppState bullet) {
        this.rootNode = rootNode;
        this.bullet = bullet;
        this.assetManager = assetManager;
    }

    public Node generateOtto(String playerNodeName) {
        //physics cylinder that encapsulates the player
        CapsuleCollisionShape cylinder = new CapsuleCollisionShape(3f, 4f);
        //control built based on above shape
        playerControl = new CharacterControl(cylinder, 0.01f);
        //Oto cast into a node-- node this can be optimized by making the model a .j3o
        playerNode = (Node) assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        
        //adds control to node, and sets the node to be the integer associated with the client (or whatever you feed into the method)
        playerNode.addControl(playerControl);
        playerNode.setName(playerNodeName);
        
        //how tall a bump the char can step over
        playerControl.setMaxSlope(2f);
        //use vector direction to change where the model looks, vs just looking where he moves
        playerControl.setUseViewDirection(true);
        //sets initial location- note it is NOT the node's location being set
        playerControl.setPhysicsLocation(new Vector3f(0, -150, 0));
        playerControl.setJumpSpeed(20);
        
        //attach the player to the scene, and add the physics control to the physicsworld
        rootNode.attachChild(playerNode);
        getPhysicsSpace().add(playerControl);
        return playerNode;
    }

    private PhysicsSpace getPhysicsSpace() {
        return bullet.getPhysicsSpace();
    }

    public Node getPlayerNode() {
        return playerNode;
    }
}
