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
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class TerrainRenderer extends SimpleApplication {

    
  public enum WaterType  {
    NONE,
    SIMPLE,
    PURDY
  };
  
  private TerrainGenerator terGen = new TerrainGenerator();
  
  private Node waterRoot;
  private Vector4f waterColor = new Vector4f(5/255f, 36/255f, 78/255f, 1.0f);
  private float waterLevel = 0.7f;
  private WaterFilter waterFilter;
  private FilterPostProcessor waterPostProcessor;
  private WaterType waterType = WaterType.PURDY;

  public TerrainRenderer() {
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
 
    terGen.init(this);
   
    setWaterType(waterType);
    addBaseWaterPlane();
  }
  
  public void canvasResized() {
    setWaterType(getWaterType());
  }

  
  public void updateTerrain() {
    TerrainQuad terrain = terGen.getTerrain();
    if(terrain != null) {
      terrain.removeFromParent();
    }
    //for recalc of water height
    setWaterLevel(getWaterLevel());
    terrain = terGen.generateTerrain();
    rootNode.attachChild(terrain);
  }
  
  public void setSunDirection(Vector3f dir) {
    terGen.setSunDirection(dir);
    //force water update to reflect lighting
    setWaterType(getWaterType());
  }
  
  public TerrainGenerator getTerrainGenerator() {
    return terGen;
  }
   
  public float getWaterLevel() {
    return waterLevel;
  }
  
  public float getWaterHeight() {
    return terGen.getHeightScale()  * waterLevel;
  }

  public void setWaterLevel(float waterLevel) {
    this.waterLevel = waterLevel;
    terGen.setWaterHeight(terGen.getHeightScale() * waterLevel);
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
    waterPostProcessor = null;
    waterFilter = null;
  }

  private void setCameraToDefault() {
    if(flyCam != null) {
      flyCam.setMoveSpeed(850);
    }

    Vector3f camPos = new Vector3f(0, 850, -terGen.getSize() * 2f);
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

  private void createWaterFilter() {

    System.out.println("TerrainRenderer.createWaterFilter: ");
    
    waterFilter = new WaterFilter(rootNode, terGen.getSunDirection());
    
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
  
  private void addBaseWaterPlane() {
    rootNode.attachChild(createWaterPlaneGeometry(5000));
  }

  private void createWaterPlane() {
    // creating a quad to render water to
    waterRoot = new Node();
    waterRoot.attachChild(createWaterPlaneGeometry(terGen.getSize()));
    waterRoot.setLocalTranslation(new Vector3f(0, getWaterHeight(), 0));
  }
  
  private Geometry createWaterPlaneGeometry(float planeSize) {
    Geometry water = new Geometry("water", new Quad(planeSize * 2, planeSize * 2));
    water.setQueueBucket(Bucket.Transparent);
    water.setLocalTranslation(-planeSize, 0, planeSize);
    water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));

    Material waterMat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    waterMat.setColor("Color", new ColorRGBA(waterColor.x, waterColor.y, waterColor.z, 1f));
    waterMat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    water.setMaterial(waterMat);
    return water;
  }


}
