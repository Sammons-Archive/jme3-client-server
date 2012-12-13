/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

/**
 *
 * @author Ben
 */
public class BenCamera {

    private ChaseCamera chaseCam;
    private FlyByCamera flyCam;
    private Camera cam;
    private Node playerNode;
    
    public BenCamera(Node playerNode, Camera cam, FlyByCamera flyCam) {
        this.playerNode = playerNode;
        this.cam = cam;
        this.flyCam = flyCam;
    }
    
    public void setupCamera() {
        flyCam.setEnabled(false);
        chaseCam = new ChaseCamera(cam, playerNode);
        chaseCam.setEnabled(true);
        chaseCam.setTrailingEnabled(true);
        chaseCam.setTrailingSensitivity(100);
        chaseCam.setRotationSensitivity(100);
        chaseCam.setDefaultDistance(40f);
        chaseCam.setSmoothMotion(true);
        flyCam.setEnabled(false);
        flyCam.setRotationSpeed(3);
        flyCam.setZoomSpeed(0);
        cam.setParallelProjection(false);
    }

    public ChaseCamera getChaseCam() {
        return chaseCam;
    }
    
        public void handleCamera(boolean state) {
        //chaseCam.setEnabled(true);
        if (state) {
            chaseCam.setEnabled(false);
            flyCam.setEnabled(true);
        } else {
            chaseCam.setEnabled(true);
            flyCam.setEnabled(false);
        }
    }
    
}
