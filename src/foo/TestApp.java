package foo;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.RawHeightMap;
import com.jme3.texture.Image;
import com.jme3.texture.Texture2D;
import com.jme3.texture.plugins.AWTLoader;

import gen.TerrainGen;
import viewer.HeightMapRenderer;

public class TestApp extends SimpleApplication {

  public static void main(String[] args) {
    new TestApp().start();
  }

  private Material terrainMat;
  private TerrainQuad terrain;

  public TestApp() {
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

    // ------ Light
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(1, -2, 0).normalizeLocal());
    sun.setColor(ColorRGBA.White);
    rootNode.addLight(sun);

    // ------ Gen heightmap
    int size = 256;
    int heightScale = 50;
    TerrainGen tg = new TerrainGen();
    float[] heightData = tg.generateHeightmap(size, size, heightScale);

    // ------ Setup material

    HeightMapRenderer hmr = new HeightMapRenderer();
    Image isoImage = new AWTLoader().load(hmr.createImage(heightData, size, size, heightScale), true);
    Texture2D isoTex = new Texture2D(isoImage);

    terrainMat = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
    terrainMat.setTexture("DiffuseMap", isoTex);
    terrainMat.setBoolean("UseMaterialColors", true);
    terrainMat.setBoolean("VertexLighting", true);
    terrainMat.setColor("Diffuse", ColorRGBA.White); // minimum material color
    terrainMat.setColor("Specular", ColorRGBA.White); // for shininess
    terrainMat.setFloat("Shininess", 0f); // [1,128] for shininess

    // ------ Create Terrain
    int patchSize = 65;
    AbstractHeightMap heightmap = new RawHeightMap(heightData);
    terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());

    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(terrainMat);
    // terrain.setLocalTranslation(0, -100, 0);
    terrain.setLocalScale(2f, 1f, 2f);
    rootNode.attachChild(terrain);

    /** 5. The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
    control.getLodCalculator().turnOffLod();
    terrain.addControl(control);
  }

}
