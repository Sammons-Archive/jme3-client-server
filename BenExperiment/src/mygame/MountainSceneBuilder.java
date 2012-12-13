/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;

/**
 *
 * @author Ben
 */
public class MountainSceneBuilder {

    private Material terrainMaterial;
    private AssetManager assetManager;
    private TerrainQuad terrainQuad;
    private RigidBodyControl tPhysicsControl;
    private Node rootNode;
    private Camera cam;
    private DirectionalLight light;
    private BulletAppState bulletAppState;

    public MountainSceneBuilder(Node rootNode, AssetManager assetManager, Camera cam, BulletAppState bulletAppState) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;
        this.cam = cam;
        this.bulletAppState = bulletAppState;
    }

    public void createLight() {
        light = new DirectionalLight();
        Vector3f direction = new Vector3f(-0.1f, -0.7f, -1).normalizeLocal();
        light.setDirection(direction);
        light.setColor(new ColorRGBA(1, 1, 1, 1));
        rootNode.addLight(light);
    }

    public void generateMap() {
        /*bring light into being*/
        createLight();

        /*material*/
        terrainMaterial = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");
        terrainMaterial.setBoolean("useTriPlanarMapping", false);
        terrainMaterial.setBoolean("WardIso", true);
        terrainMaterial.setTexture("AlphaMap", assetManager.loadTexture("Textures/Terrain/splat/alphamap.png"));

        /*create textures*/
        Texture heightMapImage = assetManager.loadTexture("Textures/mountains512.png");
        Texture grass = assetManager.loadTexture("Textures/Terrain/splat/grass.jpg");
        Texture dirt = assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg");
        Texture rock = assetManager.loadTexture("Textures/Terrain/splat/road.jpg");
        Texture normalMap0 = assetManager.loadTexture("Textures/Terrain/splat/grass_normal.jpg");
        Texture normalMap1 = assetManager.loadTexture("Textures/Terrain/splat/dirt_normal.png");
        Texture normalMap2 = assetManager.loadTexture("Textures/Terrain/splat/road_normal.png");


        /*set Texture's wrapping*/
        grass.setWrap(Texture.WrapMode.Repeat);
        dirt.setWrap(Texture.WrapMode.Repeat);
        rock.setWrap(Texture.WrapMode.Repeat);
        normalMap0.setWrap(Texture.WrapMode.Repeat);
        normalMap1.setWrap(Texture.WrapMode.Repeat);
        normalMap2.setWrap(Texture.WrapMode.Repeat);

        /*assign textures*/
        terrainMaterial.setTexture("DiffuseMap", grass);
        terrainMaterial.setFloat("DiffuseMap_0_scale", 64);
        terrainMaterial.setTexture("DiffuseMap_1", dirt);
        terrainMaterial.setFloat("DiffuseMap_1_scale", 16);
        terrainMaterial.setTexture("DiffuseMap_2", rock);
        terrainMaterial.setFloat("DiffuseMap_2_scale", 128);
        terrainMaterial.setTexture("NormalMap", normalMap0);
        terrainMaterial.setTexture("NormalMap_1", normalMap1);
        terrainMaterial.setTexture("NormalMap_2", normalMap2);

        /*Generate height map*/
        AbstractHeightMap heightMap = null;
        try {
            heightMap = new ImageBasedHeightMap(heightMapImage.getImage(), 0.25f);
            heightMap.unloadHeightMap();
            //heightMap.setHeightScale(1);
            heightMap.load();
        } catch (Exception e) {
        }
        /*prepare Terrain*/
        terrainQuad = new TerrainQuad("terrain", 65, 513, heightMap.getHeightMap());
        TerrainLodControl tLodControl = new TerrainLodControl(terrainQuad, cam);
        terrainQuad.addControl(tLodControl);
        terrainQuad.setMaterial(terrainMaterial);
        terrainQuad.setLocalScale(new Vector3f(2, 2, 2));

        /*give terrain physics*/
        tPhysicsControl = new RigidBodyControl(CollisionShapeFactory.createMeshShape(terrainQuad), 0);
        terrainQuad.addControl(tPhysicsControl);

        /*add terrain to world*/
        rootNode.attachChild(terrainQuad);
        getPhysicsSpace().add(tPhysicsControl);
        tPhysicsControl.setPhysicsLocation(new Vector3f(0, -200, 0));
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    public TerrainQuad getTerrainQuad() {
        return terrainQuad;
    }

    public RigidBodyControl gettPhysicsControl() {
        return tPhysicsControl;
    }

    public DirectionalLight getLight() {
        return light;
    }
    
    
}
