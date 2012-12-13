package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.ConnectionListener;
import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.Server;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.JmeContext;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.interfaces.GeneralMessage;
import mygame.interfaces.NewConnectionMessage;

/**
 * test
 *
 * @author ben sammons server?
 */
public class ServerMain extends SimpleApplication {

    /*Declare Variables*/
    BulletAppState bullet;
//Scene&Env.
    DirectionalLight light;
    TerrainQuad terrainQuad;
    RigidBodyControl tPhysicsControl;
    Material terrainMaterial;
//Player
    Node playerNode[] = new Node[10];
    CharacterControl playerControl[] = new CharacterControl[10];
    Material playerMat[] = new Material[10];
// anim
//Variables to help the player move
    Vector3f position = new Vector3f(0, 0, 0);
    Server myServer = null;
    int sent = 0;
    int ticks = 0;
    int newestConn = 0;
    int playerStates[] = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};

    public static void main(String[] args) {
        ServerMain app = new ServerMain();
        app.start(JmeContext.Type.Headless);//headless type is for servers
    }

    @Override
    public void simpleInitApp() {

        try {
            myServer = Network.createServer(6143);
        } catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        //buildScene();
        myServer.start();
        Serializer.registerClass(GeneralMessage.class);
        Serializer.registerClass(NewConnectionMessage.class);
        myServer.addMessageListener(new ServerListener(), GeneralMessage.class);
        myServer.addMessageListener(new ServerListener(), NewConnectionMessage.class);

        ConnectionListener scout = new ConnectionListener() {
            public void connectionAdded(Server server, HostedConnection conn) {
                newestConn = conn.getId();
            }

            public void connectionRemoved(Server server, HostedConnection conn) {
                //myServer.;
            }
        };

        myServer.addConnectionListener(scout);
    }

    @Override
    public void simpleUpdate(float tpf) {

        if (ticks < 1) {
            ticks = 5;
            for (int i = 0; i < playerStates.length; i++) {
                if (myServer.getConnection(i) != null) {
                    if (myServer.getConnection(i).getAttribute("pos") != null) {
                        playerStates[i] = 1;
                        GeneralMessage temp = new GeneralMessage();
                        temp.updateMessagePlayerPosition((Vector3f) myServer.getConnection(i).getAttribute("pos"));
                        temp.updateMessageViewTheta((Float)myServer.getConnection(i).getAttribute("vdir"));
                        temp.updateMessageWalkDirection((Vector3f) myServer.getConnection(i).getAttribute("wdir"));
                        temp.updateWalking((Boolean)myServer.getConnection(i).getAttribute("wb"));
                        temp.updateMessagePlayerID(i);
                        temp.updateMessageNumPlayers(myServer.getConnections().size());
                        temp.updateNewestPlayer(newestConn);
                        temp.updatePlayerList(playerStates);
                        myServer.broadcast(temp);
                        System.out.println("Sent message about Client #" + temp.getClientID());
                    }
                } else {
                    playerStates[i] = -1;
                }
            }
        }
        ticks--;
    }

    @Override
    public void simpleRender(RenderManager rm) {
    }

    private PhysicsSpace getPhysicsSpace() {
        return bullet.getPhysicsSpace();
    }

    public class ServerListener implements MessageListener<HostedConnection>, ClientStateListener {

        public void messageReceived(HostedConnection source, Message m) {
            if (m instanceof GeneralMessage) {
                GeneralMessage helloMessage = (GeneralMessage) m;
                helloMessage.updateMessageNumPlayers(myServer.getConnections().size());
                helloMessage.updateMessagePlayerID(source.getId());
                //inputs to server on client info
                myServer.getConnection(source.getId()).setAttribute("pos", helloMessage.getPos());
                myServer.getConnection(source.getId()).setAttribute("wdir", helloMessage.getWalkDirection());
                myServer.getConnection(source.getId()).setAttribute("vdir", helloMessage.getViewTheta());
                myServer.getConnection(source.getId()).setAttribute("wb", helloMessage.getWalkState());
                System.out.println("Recieved message from Client #" + source.getId());
            } else {
                //if invalid message
            }
        } // else....

        public void clientConnected(Client c) {
            NewConnectionMessage hi = new NewConnectionMessage();
            hi.setNewPlayerID(c.getId());
            //myServer.broadcast(hi);
            myServer.getConnection(c.getId()).setAttribute("pos", null);
            myServer.getConnection(c.getId()).setAttribute("wdir", null);
            myServer.getConnection(c.getId()).setAttribute("vdir", null);
            myServer.getConnection(c.getId()).setAttribute("wb", null);
        }

        public void clientDisconnected(Client c, DisconnectInfo info) {
        }
    }
}
