package render;

import java.awt.Canvas;

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

import render.CameraStateIO.CameraState;
import util.TypeUtil;

public class WorldRenderer extends SimpleApplication {

  public enum WaterType {
    NONE,
    SIMPLE,
    PURDY,
    TERRAIN
  };
  
  public enum ViewType {
    THREE_D,
    TWO_D,
  }

  private TerrainGenerator terGen;
  private FilterPostProcessor postProcessor;
  private ColorFilter colorFilter;

  private float waterLevel = 0.7f;
  private WaterType waterType = WaterType.PURDY;
  
  private Node simpleWaterRoot;
  private Vector4f simpleWaterColor = new Vector4f(5 / 255f, 36 / 255f, 78 / 255f, 1.0f);
  private Material simpleWaterMaterial;
  
  private PurdyWater purdyWater = new PurdyWater();
  
  private OrthoCamAppState orthCamState;
  private OrthoCameraController orthController;
  private ViewType viewType = ViewType.THREE_D;

  private CameraState prevCamState2d;
  private CameraState prevCamState3d;
  

  public WorldRenderer() {
    super(new FlyCamAppState(), new OrthoCamAppState());
    orthCamState = stateManager.getState(OrthoCamAppState.class);
    
    terGen = new TerrainGenerator();
    colorFilter = new ColorFilter();
    colorFilter.setEnabled(false);
  }

  @Override
  public void initialize() {
    if (settings != null) {
      context.getSettings().setAudioRenderer(null);
      context.getSettings().setFrameRate(160);
    }
    super.initialize();

  }

  @Override
  public void simpleInitApp() {

    orthController = new OrthoCameraController(this);
    orthCamState.setController(orthController);
    orthCamState.setEnabled(false);

    setCameraToDefault();

    setViewType(getViewType(), true);

    terGen.init(this);
    purdyWater.init(this);
    

    postProcessor = new FilterPostProcessor(assetManager);
    int numSamples = getContext().getSettings().getSamples();
    if (numSamples > 0) {
      postProcessor.setNumSamples(numSamples);
    }
    viewPort.addProcessor(postProcessor);
        
    postProcessor.addFilter(purdyWater.getWaterFilter());
    postProcessor.addFilter(colorFilter);
    postProcessor.addFilter(new FXAAFilter());
        
    setWaterType(getWaterType());
    setWaterLevel(waterLevel);
    
  }

  public void setViewType(ViewType type) {
    setViewType(type, false);
  }

  public void setViewType(ViewType type, boolean force) {
    if (viewType == type && !force) {
      return;
    }
    ViewType oldType = viewType;
    this.viewType = type;

    // save current state
    if (oldType == ViewType.THREE_D) {
      prevCamState3d = CameraStateIO.saveState(cam);
    } else {
      prevCamState2d = CameraStateIO.saveState(cam);
    }

    if (viewType == ViewType.THREE_D) {
      stateManager.getState(FlyCamAppState.class).setEnabled(true);
      orthCamState.setEnabled(false);
      CameraStateIO.applyState(cam, prevCamState3d);
    } else {
      stateManager.getState(FlyCamAppState.class).setEnabled(false);
      orthCamState.setEnabled(true);
      CameraStateIO.applyState(cam, prevCamState2d);
    }
  }

  public ViewType getViewType() {
    return viewType;
  }

  public void canvasResized(Canvas canvas) {
    cam.resize(canvas.getWidth(), canvas.getHeight(), true);
  }

  public void updateTerrain() {
    TerrainQuad terrain = terGen.getTerrain();
    if (terrain != null) {
      terrain.removeFromParent();
    }
    // for recalc of water height
    setWaterLevel(getWaterLevel());
    terrain = terGen.generateTerrain();
    rootNode.attachChild(terrain);
  }

  public void setSunDirection(Vector3f dir) {
    terGen.setSunDirection(dir);
    // force water update to reflect lighting
    setWaterType(getWaterType());
  }

  public TerrainGenerator getTerrainGenerator() {
    return terGen;
  }

  public PurdyWater getPurdyWater() {
    return purdyWater;
  }

  public ColorFilter getColorFilter() {
    return colorFilter;
  }

  public float getWaterLevel() {
    return waterLevel;
  }

  public float getWaterHeight() {
    return terGen.getRenderedHeightScale() * waterLevel;
  }

  public void setWaterLevel(float waterLevel) {
    this.waterLevel = waterLevel;
    terGen.setWaterHeight(terGen.getHeightScale() * waterLevel);
    if (simpleWaterRoot != null) {
      simpleWaterRoot.setLocalTranslation(new Vector3f(0, getWaterHeight() - 0.1f, 0));
    }
    purdyWater.updateWaterFilter();
  }

  public WaterType getWaterType() {
    return waterType;
  }

  public void setWaterType(WaterType waterType) {
    this.waterType = waterType;
    // clear current water
    removeWaterPlane();
    if (waterType == WaterType.SIMPLE) {
      addWaterPlane();
    }
    
    terGen.setDiscardWater(waterType == WaterType.NONE);
    purdyWater.setEnabled(waterType == WaterType.PURDY);
  }

  private void addWaterPlane() {
    if (simpleWaterRoot == null) {
      createWaterPlane();
    }
    rootNode.attachChild(simpleWaterRoot);
  }

  private void removeWaterPlane() {
    if (simpleWaterRoot != null) {
      simpleWaterRoot.removeFromParent();
    }
  }
  
  public Vector4f getSimpleWaterColor() {
    return simpleWaterColor;
  }

  public void setSimpleWaterColor(Vector4f simpleWaterColor) {
    this.simpleWaterColor = simpleWaterColor;
    if(simpleWaterMaterial != null) {
      simpleWaterMaterial.setColor("Color", TypeUtil.getColorRGBA(simpleWaterColor));
    }
  }

  private void setCameraToDefault() {
    if (flyCam != null) {
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


  private void createWaterPlane() {
    // creating a quad to render water to
    simpleWaterRoot = new Node();
    simpleWaterRoot.attachChild(createWaterPlaneGeometry(terGen.getSize()));
    simpleWaterRoot.setLocalTranslation(new Vector3f(0, getWaterHeight(), 0));
  }

  private Geometry createWaterPlaneGeometry(float planeSize) {
    Geometry water = new Geometry("water", new Quad(planeSize * 2, planeSize * 2));
    water.setQueueBucket(Bucket.Transparent);
    water.setLocalTranslation(-planeSize, 0, planeSize);
    water.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.HALF_PI, Vector3f.UNIT_X));

    simpleWaterMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
    simpleWaterMaterial.setColor("Color", new ColorRGBA(simpleWaterColor.x, simpleWaterColor.y, simpleWaterColor.z, 1f));
    simpleWaterMaterial.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    water.setMaterial(simpleWaterMaterial);
    return water;
  }

}
