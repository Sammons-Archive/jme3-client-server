package mygame;

import mygame.interfaces.GeneralMessage;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;
import com.jme3.network.Client;
import com.jme3.network.ClientStateListener;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.network.Network;
import com.jme3.network.serializing.Serializer;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.system.JmeContext;
import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import mygame.interfaces.NewConnectionMessage;

/**
 * Experimenting with JME3 and SpiderMonkey
 *
 * @author Ben Sammons
 */
public class Main extends SimpleApplication {

    /*Declare Variables*/
    BulletAppState bullet;
    MountainSceneBuilder map;
    BenInputs inputMaps;
    int numServerConnections = 0;
    BenCamera camera;
    Client myClient = null;
    static Main app;
    boolean initialized = false;
    Player players[] = new Player[10];
    int ticks = 0;
    final int guard[] = {0, 0, 0};

    public static void main(String[] args) {
        app = new Main();
        app.start(/*JmeContext.Type.Display*/);
    }

    @Override
    public void simpleInitApp() {

        try {
            myClient = Network.connectToServer("localHost", 6143);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        Serializer.registerClass(GeneralMessage.class);
        myClient.addMessageListener(new ClientListener(), GeneralMessage.class);
        Serializer.registerClass(NewConnectionMessage.class);
        myClient.addMessageListener(new ClientListener(), NewConnectionMessage.class);

        myClient.start();
        while (!myClient.isConnected()) {
            //this will loop until the client is fully connected with server- so the below myClient.getIDs dont return -1
        }

        //physics
        bullet = new BulletAppState();
        stateManager.attach(bullet);

        //generate map w/ light
        map = new MountainSceneBuilder(rootNode, assetManager, cam, bullet);
        map.createLight();
        map.generateMap();

        //make current player
        players[myClient.getId()] = new Player(rootNode, assetManager, bullet, inputManager, Integer.toString(myClient.getId()));
        camera = new BenCamera(players[myClient.getId()].getPlayerNode(), cam, flyCam);
        camera.setupCamera();
        inputMaps = new BenInputs(inputManager, this);

        //so clicking on desktop doesnt pause game
        app.setPauseOnLostFocus(false);
        //so game knows the initializing methods above have processed
        app.initialized = true;


    }

    @Override
    public void simpleUpdate(float tpf) {
        if (myClient.isConnected()) {//most actions are only done while the client is linked with the server
            terminateLeavers();
            checkForDoubles();
            updateLocalMessage();

            camera.handleCamera(inputMaps.getToggleCam());
            moveAllPlayers();
            sendOffMessage();
        }
        if (!myClient.isConnected() && app.initialized || !app.viewPort.isEnabled()) {
            app.stop();
        }
    }

    //sends the updated message back to the server every 5th loop of the client
    private void sendOffMessage() {
        if (ticks
                < 1) {
            ticks = 5;
            myClient.send(players[myClient.getId()].getMessage());
        }
        ticks--;

    }

    //checks for and removes players no longer connected with the server from the scenegraph
    private void terminateLeavers() {
        for (int i = 0; i < players[myClient.getId()].getMessage().getPlayerList().length; i++) {
            if (players[myClient.getId()].getMessage().getPlayerList()[i] == -1) {
                if (rootNode.getChild(Integer.toString(i)) != null) {
                    final int temp = i;
                    if (guard[0] <= 0) {
                        guard[0] = 1;
                        app.enqueue(new Callable<Object>() {
                            public Object call() throws Exception {
                                players[temp] = null;
                                return rootNode.getChild(Integer.toString(temp)).removeFromParent();
                            }
                        });
                        System.out.println("TERMINATE LEAVER #" + temp);
                    }
                    guard[0]--;
                }
            }
        }
    }

    //my current logic for generating players tends to cause duplicates == the number of clients logged in before the current one
    //I haven't fixed it yet, so this method cleans up after me
    private void checkForDoubles() {
        String names[] = new String[rootNode.getChildren().size()];
        String dup = null;
        final String temp;
        for (int i = 0; i < rootNode.getChildren().size(); i++) {
            names[i] = rootNode.getChild(i).getName();
        }
        for (int i = 0; i < names.length; i++) {
            for (int j = 0; j < names.length; j++) {
                if (i != j) {
                    if (names[i].equals(names[j])) {
                        dup = names[j];
                    }
                }
            }
        }
        temp = dup;
        if (temp != null) {
            if (guard[1] <= 0) {
                guard[1] = 10;
                app.enqueue(new Callable<Object>() {
                    public Object call() throws Exception {
                        return rootNode.getChild(temp).removeFromParent();
                    }
                });
                System.out.println("Remove Duplication of player #" + temp);
            }
            guard[1]--;
        }
    }

    //generates a player on the scenegraph
    private void addPlayer(int ID) {
        final int nameInt;
        nameInt = ID;
        if (guard[2] <= 0) {
            guard[2] = 1;
            app.enqueue(new Callable<Object>() {
                public Object call() throws Exception {
                    return players[nameInt] = new Player(rootNode, assetManager, bullet, inputManager, Integer.toString(nameInt));
                }
            });
            System.out.println("NEW PLAYER ADDED #" + ID);
        }
        guard[2]--;
    }

    //updates the local copy of the message for the current player before sending it to the server
    private void updateLocalMessage() {
        players[myClient.getId()].getMessage().updateMessagePlayerPosition(players[myClient.getId()].getPlayerNode().getControl(CharacterControl.class).getPhysicsLocation());
        players[myClient.getId()].getMessage().updateMessageViewTheta(inputMaps.getViewTheta());
        players[myClient.getId()].getMessage().updateMessageWalkDirection(inputMaps.getWalkDirection());
        players[myClient.getId()].getMessage().updateWalking(inputMaps.isWalking());
    }

    //iterates through the list of players, and if they are connected it adjusts them according to their message
    private void moveAllPlayers() {
        for (int i = 0; i < players.length; i++) {
            final int temp = i;
            if (!(players[i] == null)) {
                players[temp].move(players[temp].getMessage().getPos(), players[temp].getMessage().getViewTheta(), players[temp].getMessage().getWalkState(),
                        players[temp].getMessage().getWalkDirection());
            }
        }
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    //class required for any client game app-- note the messenger class registrations with the serializer
    //and how the client listener is registered as a listener for each method
    //this is like how you register a keymap, then make an analogListener
    //and register the string for the keymap to the listener with the inputManager
    public class ClientListener implements MessageListener<Client>, ClientStateListener {

        public void messageReceived(Client source, Message message) {
            if (initialized) {
                if (message instanceof GeneralMessage) {
                    GeneralMessage update = (GeneralMessage) message;
                    if (players[update.getClientID()] == null) {
                        addPlayer(update.getClientID());
                    } else {
                        players[update.getClientID()].setMessage(update);
                    }
                }
            }


        }

        public void clientConnected(Client c) {
        }

        public void clientDisconnected(Client c, DisconnectInfo info) {
        }
    }
}
