/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.input.InputManager;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

/**
 *
 * @author Ben
 */
public class PlayerController implements AnimEventListener {

    private AnimChannel Achannel;
    private AnimControl Acontrol;
    private float tick = 0, threshhold = 75;
    private CharacterControl player;
    private InputManager inputManager;
    private boolean walking = false, done = true, wasWalking = false, doneJumping = true;
    private Vector3f oldPos = new Vector3f(0, 0, 0);
    private Vector3f newPos = new Vector3f(0, 0, 0);
    private Vector3f pos = new Vector3f(0, 0, 0);
    private float tempView = 0;
    private float oldView = 0;
    private Vector3f newView = new Vector3f();
    private Vector3f view = new Vector3f(0, 0, 0);

    public PlayerController(InputManager inputManager, Node playerNode) {
        this.inputManager = inputManager;
        Acontrol = playerNode.getControl(AnimControl.class);
        setup();
    }

    private void setup() {
        Acontrol.addListener(this);
        Achannel = Acontrol.createChannel();
        Achannel.setAnim("stand");
    }

    public void movePlayer(Node playerNode, Vector3f walkDirection, boolean walking, float theta, Vector3f latestPos, float avgTix) {
        player = playerNode.getControl(CharacterControl.class);
        oldPos = newPos;
        newPos = latestPos;
        pos = oldPos.interpolate(newPos, 0.05f);

        //System.out.println(oldPos + "   " + newPos + "    " + pos);
        if (player.getPhysicsLocation().subtract(latestPos).length() > 1) {
            player.setPhysicsLocation(pos);
        }
        oldView = tempView;
        tempView = theta;
        tempView = ((tempView - oldView)/(avgTix))*2 + oldView;
        newView.x = (float) Math.cos(tempView);
        newView.z = (float) Math.sin(tempView);
        //view.interpolate(oldView, newView, 0.001f);
        player.setViewDirection(newView);

        boolean jump = false;


        player.setWalkDirection(walkDirection);
        //player.setViewDirection(ViewDirection);

        wasWalking = walking;
        if (done) {
            if ((!walking) || tick == threshhold - 15) {
                player.setWalkDirection(Vector3f.ZERO);
                if (!Achannel.getAnimationName().equals("stand")) {
                    Achannel.setAnim("stand", 0.2f);
                    Achannel.setLoopMode(LoopMode.DontLoop);
                }
            } else if ((walking || tick < threshhold / 3)) {
                if (!Achannel.getAnimationName().equals("Walk")) {
                    //done = false;
                    Achannel.setAnim("Walk", 0.5f);
                    Achannel.setLoopMode(LoopMode.Loop);
                }
            }

            if ((tick == threshhold) && doneJumping) {
                if (!Achannel.getAnimationName().equals("Dodge")) {
                    doneJumping = false;
                    Achannel.setAnim("Dodge", 0.1f);
                    Achannel.setLoopMode(LoopMode.DontLoop);

                }
            }
            if (tick > 0) {
                tick = tick - (0.01f);//+ (tpf * 100));
            }
            if (tick > threshhold * 3 / 4 && tick < (threshhold * 3 / 4) + 1f) {
                player.jump();
            }
            // System.out.println(tick);
            if (tick <= 0 && jump) {

                doneJumping = true;
                tick = threshhold;
            }
        }
    }

    public void giveLatestNews(Vector3f pos, Vector3f wdir, Vector3f vdir) {
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {
        if (!wasWalking) {
            done = true;
        }
    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
}
