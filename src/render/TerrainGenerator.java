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
import gen.HeightMapUtil;
import gen.SimplexNoiseGen;

public class TerrainGenerator {

  private Material terrainMat;
  private TerrainQuad terrain;
  private SimplexNoiseGen noiseGen = new SimplexNoiseGen();

  private int size = 512;
  private float heightScale = 40;
  private String baseHeightMapSource = "textures/circleGradLarge.png";
  private float noiseRatio = 1;
  private float erodeFilter = 0;

  private String hipsoTex = "textures/hipso_one.png";

  private Vector4f waterColor = new Vector4f(5 / 255f, 36 / 255f, 78 / 255f, 1.0f);
  private String bathTex = "textures/bath_dark.png";

  private Vector3f sunDir = new Vector3f(1, -1, 0).normalizeLocal();

  private TerrainRenderer renderer;
  private float waterHeight = 20;

  public void init(TerrainRenderer app) {
    this.renderer = app;
    createTerrainMaterial();
  }

  public TerrainQuad generateTerrain() {

    // float[] heightData = terrainGen.generateHeightmap(size, size, heightScale);

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
    HeightMapUtil.scale(heightData, heightScale);
    logTime("Mix: ", t1);

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

  private void logTime(String string, long t1) {
    System.out.println("TerrainApp.logTime: " + string + ": " + (System.currentTimeMillis() - t1));
  }

  private void createTerrainMaterial() {

    terrainMat = new Material(renderer.getAssetManager(), "materials/terrain.j3md");
    terrainMat.setFloat("WaterLevel", waterHeight);
    terrainMat.setFloat("MaxHeight", heightScale);
    terrainMat.setVector4("WaterColor", waterColor);
    terrainMat.setVector3("SunDir", sunDir);
    setHipsoTexture(hipsoTex);
    setBathTexture(bathTex);

  }
  
  public void setWaterHeight(float waterHeight) {
    this.waterHeight  = waterHeight;
    if(terrainMat != null) {
      terrainMat.setFloat("WaterLevel", waterHeight);
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
    if(terrainMat != null) {
      terrainMat.setFloat("MaxHeight", heightScale);
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
    
    double spacing = 2048d/size;
    noiseGen.setSampleSpacing(spacing);
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
  
  
  
  

}