package render;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.RawHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

import crap.ImageHeightmapLoader;
import noise.TerrainGen;

public class TerrainApp extends SimpleApplication {

  private Material terrainMat;
  private TerrainQuad terrain;
  private TerrainGen terrainGen = new TerrainGen();
  private Node waterRoot;

  private int size = 512;
  private float heightScale = 40;
  private float waterLevel = 0.7f;
  private Vector3f sunDir = new Vector3f(1, -1, 0).normalizeLocal();
  
  public enum WaterType  {
    NONE,
    SIMPLE,
    PURDY
  };
  
  private Vector4f waterColor = new Vector4f(5/255f, 36/255f, 78/255f, 1.0f);

  private WaterFilter waterFilter;
  private FilterPostProcessor waterPostProcessor;
  private WaterType waterType = WaterType.PURDY;
  private String hipsoTex = "textures/hipso_one.png";
  private String bathTex = "textures/bath_dark.png";

  public TerrainApp() {
//     super((AppState[])null);
    super(new FlyCamAppState());
  }

  @Override
  public void initialize() {
    if(settings != null) {
      context.getSettings().setAudioRenderer(null);
      context.getSettings().setFrameRate(160);
    }
    super.initialize();
  }
  
  @Override
  public void simpleInitApp() {
   
    setCameraToDefault();

    //Add AA
    FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
    fpp.addFilter(new FXAAFilter());
    int numSamples = getContext().getSettings().getSamples();
    if (numSamples > 0) {
        fpp.setNumSamples(numSamples);
    }
    viewPort.addProcessor(fpp);
 
    createTerrainMaterial();
   
    setWaterType(waterType);
  }
  
  public void canvasResized() {
    setWaterType(getWaterType());
  }

  public int getTerainSize() {
    return size;
  }
  
  public TerrainGen getTerrainGen() {
    return terrainGen;
  }

  public void updateTerrain() {
    terrain.removeFromParent();
    generateTerrain();
  }

  public void updateTerrain(long seed) {
    terrainGen.setSeed(seed);
    updateTerrain();
  }

  public void updateTerrain(int size, int octaves, double roughness, double scale, float erode) {
    this.size = size;
    terrainGen.setOctaves(octaves);
    terrainGen.setRoughness(roughness);
    terrainGen.setScale(scale);
    if (terrain != null) {
      terrain.removeFromParent();
    }
    generateTerrain(erode);

  }
  
  public void setHipsoTexture(String tex) {
    hipsoTex = tex;
    if(terrainMat != null) {
      Texture isoTex = assetManager.loadTexture(hipsoTex);
      terrainMat.setTexture("ColorMap", isoTex);
    }
  }
  
  public String getHipsoTex() {
    return hipsoTex;
  }
  
  public void setBathTexture(String tex) {
    bathTex = tex;
    if(terrainMat != null) {
      Texture bathTex = assetManager.loadTexture(tex);
      terrainMat.setTexture("WaterColorMap", bathTex);
    }
  }
  
  public String getBathTexture() {
    return bathTex;
  }

  public void setSunDirection(Vector3f dir) {
    sunDir.set(dir);
    if (terrainMat != null) {
      terrainMat.setVector3("SunDir", dir.normalizeLocal());
    }
  }

  public float getHeightScale() {
    return heightScale;
  }

  public void setHeightScale(float heightScale) {
    this.heightScale = heightScale;
    if(terrainMat != null) {
      terrainMat.setFloat("MaxHeight", heightScale);
    }
    //for recalc of water height
    setWaterLevel(getWaterLevel());
  }

  public float getWaterLevel() {
    return waterLevel;
  }
  
  public float getWaterHeight() {
    return heightScale * waterLevel;
  }

  public void setWaterLevel(float waterLevel) {
    this.waterLevel = waterLevel;
    if (terrainMat != null) {
      terrainMat.setFloat("WaterLevel", heightScale * waterLevel);
    }
    if(waterRoot != null) {
      waterRoot.setLocalTranslation(new Vector3f(0, getWaterHeight(), 0));
    }
    if(waterFilter != null) {
      waterFilter.setWaterHeight(getWaterHeight());
    }
  }

  public WaterType getWaterType() {
    return waterType;
  }

  public void setWaterType(WaterType waterType) {
    this.waterType = waterType;
    //clear current water
    removeWaterPlane();
    removeWaterFilter();
    if(waterType == WaterType.SIMPLE) {
      addWaterPlane();
    } else if(waterType == WaterType.PURDY) {
      addWaterFilter();
    }
  }
  
  private void addWaterPlane() {
    if(waterRoot == null) {
      createWaterPlane();
    }
    rootNode.attachChild(waterRoot);
  }
  
  private void removeWaterPlane() {
    if(waterRoot != null) {
      waterRoot.removeFromParent();
    }
  }
  
  private void addWaterFilter() {
    if(waterFilter == null) {
      createWaterFilter();
    }
    waterPostProcessor = new FilterPostProcessor(assetManager);
    waterPostProcessor.addFilter(waterFilter);
    viewPort.addProcessor(waterPostProcessor);
  }

  private void removeWaterFilter() {
    if(waterPostProcessor != null) {
      viewPort.removeProcessor(waterPostProcessor);
    }
  }

  private void setCameraToDefault() {
    if(flyCam != null) {
      flyCam.setMoveSpeed(850);
    }

    Vector3f camPos = new Vector3f(0, 850, -size * 2f);
    cam.setLocation(camPos);

    Vector3f dir = new Vector3f(camPos);
    dir.multLocal(-1);
    dir.normalizeLocal();

    Quaternion q = new Quaternion();
    // left, up, dir
    q.fromAxes(new Vector3f(-1, 0, 0), new Vector3f(0, 1, 0), dir);
    q.normalizeLocal();
    cam.setAxes(q);
    
    cam.setFrustumFar(4000);
  }
  
  private void createTerrainMaterial() {

    terrainMat = new Material(assetManager, "materials/terrain.j3md");
    terrainMat.setFloat("WaterLevel", getWaterHeight());
    terrainMat.setFloat("MaxHeight", heightScale);
    terrainMat.setVector4("WaterColor", waterColor);
    terrainMat.setVector3("SunDir", sunDir);
    setHipsoTexture(hipsoTex);
    setBathTexture(bathTex);
    
  }

  private void createWaterFilter() {

    waterFilter = new WaterFilter(rootNode, sunDir);
    
    waterFilter.setWaterColor(new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f));
    waterFilter.setDeepWaterColor(new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f));
    waterFilter.setUnderWaterFogDistance(80);
    waterFilter.setWaterTransparency(0.12f);
    waterFilter.setFoamIntensity(0.4f);
    waterFilter.setFoamHardness(0.3f);
    waterFilter.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
    waterFilter.setReflectionDisplace(50);
    waterFilter.setRefractionConstant(0.25f);
    waterFilter.setColorExtinction(new Vector3f(30, 50, 70));
    waterFilter.setCausticsIntensity(0.4f);
    waterFilter.setWaveScale(0.003f);
    waterFilter.setMaxAmplitude(2f);
    waterFilter.setFoamTexture((Texture2D) assetManager.loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
    waterFilter.setRefractionStrength(0.2f);
    waterFilter.setWaterHeight(getWaterHeight());
    
    waterFilter.setFoamIntensity(0.2f);
//    waterFilter.setFoamExistence(new Vector3f(0.8f, 8f, 1f));
    waterFilter.setFoamExistence(new Vector3f(0.6f, 6f, 0.1f));
    
//    waterFilter.setWaveScale(0.001f);
    waterFilter.setWaveScale(0.0f);

  }
  
  private void createWaterPlane() {
    // creating a quad to render water to
    Geometry water = new Geometry("water", new Quad(size * 2, size * 2));
    water.setQueueBucket(Bucket.Transparent);
    water.setLocalTranslation(-size, 0, size);
    water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));

    Material waterMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    waterMat.setColor("Color", new ColorRGBA(waterColor.x, waterColor.y, waterColor.z, 1f));
    waterMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    water.setMaterial(waterMat);

    waterRoot = new Node();
    waterRoot.attachChild(water);
    waterRoot.setLocalTranslation(new Vector3f(0, getWaterHeight(), 0));
  }

  private void generateTerrain() {
    generateTerrain(-1);
  }

  private void generateTerrain(float erodeFilter) {

//    float[] heightData = terrainGen.generateHeightmap(size, size, heightScale);
    float[] heightData = ImageHeightmapLoader.loadGrayScaleData("textures/circleGradLarge.png", heightScale);
    
    float[] randData = terrainGen.generateHeightmap(size, size, heightScale);
    
    for(int i=0;i<randData.length;i++) {
      heightData[i] += randData[i];
      heightData[i] /= 2;
    }
    

    // ------ Create Terrain
     
    AbstractHeightMap heightmap;
    heightmap = new RawHeightMap(heightData);
    
//    Texture heightMapImage = assetManager.loadTexture(
//        "textures/circleGradLarge.png");
//    heightmap = new ImageBasedHeightMap(heightMapImage.getImage(), 100);

    if (erodeFilter >= 0 && erodeFilter <= 1) {
      try {
        heightmap.setMagnificationFilter(erodeFilter);
        heightmap.erodeTerrain();
      } catch (Exception e) {
        e.printStackTrace();
      }

    }

    int patchSize = 65;
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

  

}
