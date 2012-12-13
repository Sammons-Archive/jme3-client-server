/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;

/**
 * This class will take inputs and give out everything a player should need to
 * turn/walk/ jump(eventually)
 *
 * @author Ben
 */
class BenInputs {

    private InputManager inputManager;
    private float theta = 0f;
    private Vector3f viewDirection = new Vector3f();
    private Vector3f walkDirection = new Vector3f();
    private boolean clicked = false, jump = false, walking = false, walkingV = false;
    private float tpfs;
    private Main main;

    public BenInputs(InputManager inputManager, Main main) {
        this.inputManager = inputManager;
        this.main = main;
        setupKeys();
    }

    private void setupKeys() {
        //all the input maps I thought I might need at first
        inputManager.addMapping("tCharLeft", new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("tCharRight", new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("mCharLeft", new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping("mCharRight", new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping("mCharForward", new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("mCharBack", new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("zoomCamf", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
        inputManager.addMapping("zoomCamb", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
        inputManager.addMapping("turnCam", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addMapping("esc", new KeyTrigger(KeyInput.KEY_ESCAPE));
        //registers the maps above to a particular listener
        inputManager.addListener(cameraListener, new String[]{"zoomCamf", "turnCam", "zoomCamb", "esc"});
        inputManager.addListener(analogListener, new String[]{"mCharLeft", "tCharRight", "tCharLeft", "mCharRight", "mCharBack", "mCharForward"});
        inputManager.addListener(jumpListener, "jump");
    }
    private ActionListener cameraListener = new ActionListener() {
        public void onAction(String name, boolean isPressed, float tpf) {
            tpfs = tpf;
            //this is all about the toggling of the flycam
            if (name.equals("turnCam")) {
                if (isPressed) {
                    clicked = true;
                } else {
                    clicked = false;
                }
                //die when esc is pressed
            } else if (name.equals("esc")) {
                System.exit(0);
                main.stop(false);
            }
        }
    };
    private AnalogListener jumpListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("jump")) {
                jump = true;
            } else {
                jump = false;
            }
        }
    };
    //pay attention to the booleans from here down, they are not well encapsulated and behave funny- I still need to spend time fixing these
    private AnalogListener analogListener = new AnalogListener() {
        public void onAnalog(String name, float value, float tpf) {
            tpfs = tpf;

//            if (theta >= 2 * Math.PI) {
//                theta -= 2*Math.PI;
//            } else if (theta <= -2 * Math.PI) {
//                theta += 2*Math.PI;
//            }
            if (value > 0) {
                if (name.equals("mCharForward")) {
                    walking = true;
                    walkingV = true;
                    walkDirection = viewDirection.mult((0.2f + value * 1));
                } else if (name.equals("mCharBack")) {
                    walking = true;
                    walkingV = true;
                    walkDirection = viewDirection.mult(-(0.2f + value * 1));
                } else {
                    walkingV = false;
                    walkDirection = Vector3f.ZERO;
                }

                if (name.equals("tCharRight")) {
                    walking = true;
                    theta = theta + (FastMath.PI / 45) * (tpf * 75);
                    viewDirection.x = (float) Math.cos(theta);
                    viewDirection.z = (float) Math.sin(theta);
                    if (!walkingV) {
                        walkDirection = Vector3f.ZERO;
                    }
                } else if (name.equals("tCharLeft")) {
                    walking = true;
                    theta = theta - (FastMath.PI / 45) * (tpf * 75);
                    viewDirection.x = (float) Math.cos(theta);
                    viewDirection.z = (float) Math.sin(theta);
                    if (!walkingV) {
                        walkDirection = Vector3f.ZERO;
                    }
                }

            } else {
                walkDirection = Vector3f.ZERO;
            }
        }
    };

    public float getViewTheta() {

        return theta;
    }

    public boolean isWalking() {
        if (walking) {
            walking = !walking;
            return !walking;
        } else {
            return walking;
        }
    }

    public Vector3f getWalkDirection() {
        return walkDirection;
    }

    public boolean getJumpState() {
        if (jump) {
            jump = !jump;
            return !jump;
        }
        return jump;
    }

    public boolean getToggleCam() {
        return clicked;
    }

    public float getTPF() {
        return tpfs;
    }
}
