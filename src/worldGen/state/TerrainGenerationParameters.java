package worldGen.state;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import worldGen.gen.ElevationRamp;
import worldGen.gen.ExponentialElevationRamp;
import worldGen.gen.SimplexNoiseGen;
import worldGen.render.TerrainGenerator;

public class TerrainGenerationParameters implements Savable {

  public static final int DEF_SIZE = 512;
  public static final float DEF_HEIGHT_SCALE = 300;
  public static final String DEF_HEIGHT_MAP = "textures/circleGradLarge.png";
  public static final float DEF_NOISE_RATIO = 1;
  public static final float DEF_ERODE_FILTER = 0;
  public static final float DEF_WATER_LEVEL = 0.7f;
  public static final ElevationRamp DEF_LAND_RAMP = new ExponentialElevationRamp();
  public static final ElevationRamp DEF_WATER_RAMP = new ExponentialElevationRamp(true);
  
  public static final SimplexNoiseGen DEF_NOISE = new SimplexNoiseGen();
  
  private SimplexNoiseGen noiseGen;
  
  private int size;
  private float heightScale;
  private String baseHeightMapSource;
  private float noiseRatio;
  private float erodeFilter;
  private float waterLevel;
  private ElevationRamp landElevationRamp;
  private ElevationRamp waterElevationRamp;

  public TerrainGenerationParameters() {
    noiseGen = DEF_NOISE;
    size = DEF_SIZE;
    heightScale = DEF_HEIGHT_SCALE;
    baseHeightMapSource = DEF_HEIGHT_MAP;
    noiseRatio = DEF_NOISE_RATIO;
    erodeFilter = DEF_ERODE_FILTER;
    waterLevel = DEF_WATER_LEVEL;
    landElevationRamp = DEF_LAND_RAMP;
    waterElevationRamp = DEF_WATER_RAMP;
  }
  
  public TerrainGenerationParameters(TerrainGenerator gen) {
    noiseGen = new SimplexNoiseGen(gen.getNoiseGenerator());
    size = gen.getSize();
    heightScale = gen.getHeightScale();
    baseHeightMapSource = gen.getBaseHeightMapSource();
    noiseRatio = gen.getNoiseRatio();
    erodeFilter = gen.getErodeFilter();
    waterLevel = gen.getWaterLevel();
    landElevationRamp = gen.getLandElevationRamp();
    waterElevationRamp = gen.getWaterElevationRamp();
  }
  
  public void apply(TerrainGenerator gen) {
    gen.setSize(size);
    gen.setNoiseGenerator(new SimplexNoiseGen(noiseGen));
    gen.setHeightScale(heightScale);
    gen.setBaseHeightMapSource(baseHeightMapSource);
    gen.setNoiseRatio(noiseRatio);
    gen.setErodeFilter(erodeFilter);
    gen.setWaterLevel(waterLevel);
    gen.setLandElevationRamp(landElevationRamp);
    gen.setWaterElevationRamp(waterElevationRamp);
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule cap = ex.getCapsule(this);
    cap.write(noiseGen, "noiseGen", null);
    cap.write(size, "size", -1);
    cap.write(heightScale, "heightScale", -1);
    cap.write(baseHeightMapSource, "baseHeightMapSource", null);
    cap.write(noiseRatio, "noiseRatio", -1);
    cap.write(erodeFilter, "erodeFilter", -1);
    cap.write(waterLevel, "waterLevel", -1);
    cap.write(landElevationRamp, "landElevationRamp", null);
    cap.write(waterElevationRamp, "waterElevationRamp", null);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule cap = im.getCapsule(this);
    noiseGen = (SimplexNoiseGen)cap.readSavable("noiseGen", DEF_NOISE);
    size = cap.readInt("size", DEF_SIZE);
    heightScale = cap.readFloat("heightScale", DEF_HEIGHT_SCALE);
    baseHeightMapSource = cap.readString("baseHeightMapSource", DEF_HEIGHT_MAP);
    noiseRatio = cap.readFloat("noiseRatio", DEF_NOISE_RATIO);
    erodeFilter = cap.readFloat("erodeFilter", DEF_ERODE_FILTER);
    waterLevel = cap.readFloat("waterLevel", DEF_WATER_LEVEL);
    landElevationRamp = (ElevationRamp)cap.readSavable("landElevationRamp", DEF_LAND_RAMP);
    waterElevationRamp = (ElevationRamp)cap.readSavable("waterElevationRamp", DEF_WATER_RAMP);
  }

  public SimplexNoiseGen getNoiseGen() {
    return noiseGen;
  }

  public void setNoiseGen(SimplexNoiseGen noiseGen) {
    this.noiseGen = noiseGen;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public float getHeightScale() {
    return heightScale;
  }

  public void setHeightScale(float heightScale) {
    this.heightScale = heightScale;
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

  public float getWaterLevel() {
    return waterLevel;
  }

  public void setWaterLevel(float waterLevel) {
    this.waterLevel = waterLevel;
  }

  public ElevationRamp getLandElevationRamp() {
    return landElevationRamp;
  }

  public void setLandElevationRamp(ElevationRamp landElevationRamp) {
    this.landElevationRamp = landElevationRamp;
  }

  public ElevationRamp getWaterElevationRamp() {
    return waterElevationRamp;
  }

  public void setWaterElevationRamp(ElevationRamp waterElevationRamp) {
    this.waterElevationRamp = waterElevationRamp;
  }

  @Override
  public String toString() {
    return "TerrainParameters [noiseGen=" + noiseGen + ", size=" + size + ", heightScale=" + heightScale + ", baseHeightMapSource=" + baseHeightMapSource
        + ", noiseRatio=" + noiseRatio + ", erodeFilter=" + erodeFilter + ", waterLevel=" + waterLevel + ", landElevationRamp=" + landElevationRamp
        + ", waterElevationRamp=" + waterElevationRamp + "]";
  }
  
  
  
}
