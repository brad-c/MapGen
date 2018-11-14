package foo;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.RawHeightMap;
import com.jme3.texture.Texture;

import gen.TerrainGen;

public class TerrainApp extends SimpleApplication {

  private Material terrainMat;
  private TerrainQuad terrain;
  private TerrainGen terrainGen;
  private int size = 256;
  private float heightScale = 50;
  
  //private DirectionalLight sun;

  public TerrainApp() {
    // super((AppState[])null);
    super(new FlyCamAppState());
    showSettings = false;

  }

  @Override
  public void simpleInitApp() {
    initMyTerrain();
  }

  public void initMyTerrain() {
    
    // ------ Camera
    flyCam.setMoveSpeed(850);

    Quaternion q = new Quaternion();
    // left, up, dir
    q.fromAxes(new Vector3f(-1, 0, 0), new Vector3f(0, 1, 0), new Vector3f(0, -1, 0));
    q.normalizeLocal();
    cam.setAxes(q);
    cam.setLocation(new Vector3f(0, 750, 0));

//    // ------ Light
//    sun = new DirectionalLight();
//    sun.setDirection(new Vector3f(1, -1, 0).normalizeLocal());
//    sun.setColor(ColorRGBA.White);
//    rootNode.addLight(sun);

    // ------ Gen heightmap
    terrainGen = new TerrainGen();
    generateTerrain();
  }

  
  private void generateTerrain() {
    generateTerrain(-1);
  }
  
  private void generateTerrain(float erodeFilter) {
    
    float[] heightData = terrainGen.generateHeightmap(size, size, heightScale);

    // ------ Setup material
    Texture isoTex = assetManager.loadTexture("textures/hipso.png");


    terrainMat = new Material(assetManager, "materials/terrain.j3md");
    terrainMat.setTexture("ColorMap", isoTex);
    terrainMat.setFloat("WaterLevel", heightScale * 0.5f);
    terrainMat.setFloat("MaxHeight", heightScale);
    terrainMat.setVector4("WaterColor", new Vector4f(0,0,0.5f,1));
    //terrainMat.setVector3("SunDir", new Vector3f(1, -1, 0).normalizeLocal());
    
    // ------ Create Terrain
    //int patchSize = 65;
    int patchSize = size + 1;
    AbstractHeightMap heightmap = new RawHeightMap(heightData);
    
    if(erodeFilter >= 0 && erodeFilter <=1) {
      try {
        heightmap.setMagnificationFilter(erodeFilter);
        heightmap.erodeTerrain();
      } catch (Exception e) {
        e.printStackTrace();
      }
      
    }
    
    terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());

    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(terrainMat);
    terrain.setLocalScale(2f, 1f, 2f);
    
    rootNode.attachChild(terrain);

    /** 5. The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    control.getLodCalculator().turnOffLod();
    terrain.addControl(control);
  }

  public void updateTerrain() {
    terrain.removeFromParent();
    generateTerrain();
  }
  
  public void updateTerrain(long seed) {
    terrainGen.setSeed(seed);
    updateTerrain();
  }

  public void updateTerrain(int size, int octaves, double roughness, double scale) {
    this.size = size;
    terrainGen.setOctaves(octaves);
    terrainGen.setRoughness(roughness);
    terrainGen.setScale(scale);
    updateTerrain();
    
  }
  
  public void erodeTerrain(float amount) {
    terrain.removeFromParent();
    generateTerrain(amount);
  }

  public void setSunDirection(Vector3f dir) {
    terrainMat.setVector3("SunDir", dir.normalizeLocal());
  }

  public float getHeightScale() {
    return heightScale;
  }

  public void setHeightScale(float heightScale) {
    this.heightScale = heightScale;
  }
  
  
  
}
