package render;

import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.RawHeightMap;
import com.jme3.texture.Texture;

import crap.ImageHeightmapLoader;
import gen.ElevationRamp;
import gen.ExponentialElevationRamp;
import gen.HeightMapUtil;
import gen.SimplexNoiseGen;

public class TerrainGenerator {

  private Material terrainMat;
  private TerrainQuad terrain;
  private SimplexNoiseGen noiseGen = new SimplexNoiseGen();

  private int size = 512;
  private float heightScale = 300;
  private String baseHeightMapSource = "textures/circleGradLarge.png";
  private float noiseRatio = 1;
  private float erodeFilter = 0;
  private ElevationRamp landElevationRamp = new ExponentialElevationRamp();
  private ElevationRamp waterElevationRamp = new ExponentialElevationRamp(true);

  private String hipsoTex = "textures/hipso_one.png";
  private String bathTex = "textures/bath_dark.png";
  private Vector4f waterColor = new Vector4f(5 / 255f, 36 / 255f, 78 / 255f, 1.0f);

  private boolean discardWater;

  private Vector3f sunDir = new Vector3f(1, -1, 0).normalizeLocal();
  private float ambientLight = 0.1f;

  private WorldRenderer renderer;
  private float waterHeight = 20;

  private boolean renderCoastline = false;
  private float coastlineThickness = 0.8f;
  // TODO: Make support alpha
  private Vector3f coastlineColor = new Vector3f(0, 0, 0);

  // The reference heightmap resolution used to determine the appropriate
  // horizontol spacing for noise sampling and vertical scale for rendered heights
  // to ensure the appearance of te terrain is constant at different size
  private static final int BASE_RESOLUTION = 2048;

  public void init(WorldRenderer app) {
    this.renderer = app;
    createTerrainMaterial();
  }

  public TerrainQuad generateTerrain() {

    // baseHeightMapSource =
    // "D:\\Dev\\TerrainGen\\MapGen\\resources\\textures\\gradTest.png";
    long t1 = System.currentTimeMillis();
    float[] heightData = ImageHeightmapLoader.loadGrayScaleData(baseHeightMapSource, size, 1, true);
    logTime("GS Image: ", t1);

    t1 = System.currentTimeMillis();
    float[] noiseData = noiseGen.getOrUpdateHeightMap();
    logTime("Noise Gen: ", t1);

    t1 = System.currentTimeMillis();
    for (int i = 0; i < noiseData.length; i++) {
      heightData[i] += (noiseData[i] * noiseRatio);
    }
    HeightMapUtil.normalise(heightData);
    logTime("Mix: ", t1);

    if (landElevationRamp != null) {
      t1 = System.currentTimeMillis();
      float waterRat = getRenderedWaterHeight() / getRenderedHeightScale();
      heightData = landElevationRamp.apply(heightData, waterRat, 1);
      logTime("Land Ramp: ", t1);
    }
    if (waterElevationRamp != null) {
      t1 = System.currentTimeMillis();
      float waterRat = getRenderedWaterHeight() / getRenderedHeightScale();
      heightData = waterElevationRamp.apply(heightData, 0, waterRat);
      logTime("Water Ramp: ", t1);
    }

    t1 = System.currentTimeMillis();
    HeightMapUtil.scale(heightData, getRenderedHeightScale());
    logTime("Scale: ", t1);

    // ------ Create Terrain
    AbstractHeightMap heightmap;
    heightmap = new RawHeightMap(heightData);

    if (erodeFilter > 0 && erodeFilter <= 1) {
      t1 = System.currentTimeMillis();
      try {
        heightmap.setMagnificationFilter(erodeFilter);
        heightmap.erodeTerrain();
      } catch (Exception e) {
        e.printStackTrace();
      }
      logTime("Errode: ", t1);
    }

    t1 = System.currentTimeMillis();

    // int patchSize = 65;
    int patchSize = size + 1;
    terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());

    /** 4. We give the terrain its material, position & scale it, and attach it. */
    terrain.setMaterial(terrainMat);
    terrain.setLocalScale(2f, 1f, 2f);

    // rootNode.attachChild(terrain);

    /** 5. The LOD (level of detail) depends on were the camera is: */
    TerrainLodControl control = new TerrainLodControl(terrain, renderer.getCamera());
    control.getLodCalculator().turnOffLod();
    terrain.addControl(control);

    logTime("Terrain Construction", t1);

    return terrain;

  }

  public ElevationRamp getLandElevationRamp() {
    return landElevationRamp;
  }

  public void setLandElevationRamp(ElevationRamp landElvationRamp) {
    this.landElevationRamp = landElvationRamp;
  }

  public ElevationRamp getWaterElevationRamp() {
    return waterElevationRamp;
  }

  public void setWaterElevationRamp(ElevationRamp waterElevationRamp) {
    this.waterElevationRamp = waterElevationRamp;
  }

  public float getRenderScale() {
    return (float) size / BASE_RESOLUTION;
  }

  public float getRenderedHeightScale() {
    return getRenderScale() * heightScale;
  }

  public Float getRenderedWaterHeight() {
    return getRenderScale() * waterHeight;
  }

  private void createTerrainMaterial() {

    terrainMat = new Material(renderer.getAssetManager(), "materials/terrain.j3md");

    // apply defaults
    setWaterHeight(getWaterHeight());
    setHeightScale(getHeightScale());
    setWaterHeight(getWaterHeight());
    setSunDirection(getSunDirection());
    setHipsoTexture(getHipsoTex());
    setBathTexture(getBathTexture());
    setCoastlineThickness(getCoastlineThickness());
    setRenderCoastline(isRenderCoastline());
    setCoastlineColor(getCoastlineColor());
    setAmbientLight(getAmbientLight());
    setDiscardWater(isDiscardWater());
  }

  public boolean isDiscardWater() {
    return discardWater;
  }

  public void setDiscardWater(boolean discardWater) {
    this.discardWater = discardWater;
    if (terrainMat != null) {
      terrainMat.setBoolean("DiscardWater", discardWater);
    }
  }

  public boolean isRenderCoastline() {
    return renderCoastline;
  }

  public void setRenderCoastline(boolean renderCoastline) {
    this.renderCoastline = renderCoastline;
    if (terrainMat != null) {
      terrainMat.setBoolean("RenderCoastline", renderCoastline);
    }
  }

  public float getCoastlineThickness() {
    return coastlineThickness;
  }

  public void setCoastlineThickness(float coastlineThickness) {
    this.coastlineThickness = coastlineThickness;
    if (terrainMat != null) {
      terrainMat.setFloat("CoastlineThickness", coastlineThickness);
    }
  }

  public Vector3f getCoastlineColor() {
    return coastlineColor;
  }

  public void setCoastlineColor(Vector3f col) {
    coastlineColor = col;
    if (terrainMat != null) {
      terrainMat.setVector3("CoastlineColor", coastlineColor);
    }
  }

  public void setWaterHeight(float waterHeight) {
    this.waterHeight = waterHeight;
    if (terrainMat != null) {
      terrainMat.setFloat("WaterLevel", getRenderedWaterHeight());
    }
  }

  public float getWaterHeight() {
    return waterHeight;
  }

  public Vector4f getWaterColor() {
    return waterColor;
  }

  public void setWaterColor(Vector4f waterColor) {
    this.waterColor = waterColor;
    if (terrainMat != null) {
      terrainMat.setVector4("WaterColor", waterColor);
    }
  }

  public float getAmbientLight() {
    return ambientLight;
  }

  public void setAmbientLight(float ambientLight) {
    this.ambientLight = ambientLight;
    if (terrainMat != null) {
      terrainMat.setFloat("AmbientLight", getAmbientLight());
    }
  }

  public void setSunDirection(Vector3f dir) {
    sunDir.set(dir);
    if (terrainMat != null) {
      terrainMat.setVector3("SunDir", dir.normalizeLocal());
    }
  }

  public Vector3f getSunDirection() {
    return sunDir;
  }

  public void setHeightScale(float heightScale) {
    this.heightScale = heightScale;
    if (terrainMat != null) {
      terrainMat.setFloat("MaxHeight", getRenderedHeightScale());
    }
  }

  public float getHeightScale() {
    return heightScale;
  }

  public String getBaseHeightMapSource() {
    return baseHeightMapSource;
  }

  public void setBaseHeightMapSource(String baseHeightMapSource) {
    this.baseHeightMapSource = baseHeightMapSource;
  }

  public float getNoiseRatio() {
    return noiseRatio;
  }

  public void setNoiseRatio(float noiseRatio) {
    this.noiseRatio = noiseRatio;
  }

  public float getErodeFilter() {
    return erodeFilter;
  }

  public void setErodeFilter(float erodeFilter) {
    this.erodeFilter = erodeFilter;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
    noiseGen.setSize(size);

    double spacing = BASE_RESOLUTION / size;
    noiseGen.setSampleSpacing(spacing);
    // for rescaling of height values
    setHeightScale(heightScale);
  }

  public SimplexNoiseGen getNoiseGenerator() {
    return noiseGen;
  }

  public void setHipsoTexture(String tex) {
    hipsoTex = tex;
    if (terrainMat != null) {
      Texture isoTex = renderer.getAssetManager().loadTexture(hipsoTex);
      terrainMat.setTexture("ColorMap", isoTex);
    }
  }

  public String getHipsoTex() {
    return hipsoTex;
  }

  public void setBathTexture(String tex) {
    bathTex = tex;
    if (terrainMat != null) {
      Texture bathTex = renderer.getAssetManager().loadTexture(tex);
      terrainMat.setTexture("WaterColorMap", bathTex);
    }
  }

  public String getBathTexture() {
    return bathTex;
  }

  public TerrainQuad getTerrain() {
    return terrain;
  }

  private void logTime(String string, long t1) {
    System.out.println("TerrainApp.logTime: " + string + ": " + (System.currentTimeMillis() - t1));
  }

}
