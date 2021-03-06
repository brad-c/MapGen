package worldGen.gen;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import worldGen.noise.SimplexNoise;

public class SimplexNoiseGen implements Savable {

  public static final int DEF_OCTAVES = 12;
  public static final double DEF_ROUGHNESS = 0.6;
  public static final double DEF_SCALE = 0.001;
  public static final long DEF_SEED = 8092038379555713298l;
  public static final int DEF_WIDTH = 512;
  public static final int DEF_HEIGHT = 512;
  public static final int DEF_HEIGHT_SCALE = 1;
  public static final double DEF_SAMPLE_SPACING = 1;
  
  private int octaves;
  private double roughness;
  private double scale;
  private long seed;
  
  private int width;
  private int height;
  private float heightScale;

  private double sampleSpacing;

  private transient float[] heightMap = null;
  private transient int genHash = -1;
  
  
  public SimplexNoiseGen() {
    octaves = DEF_OCTAVES;
    roughness = DEF_ROUGHNESS;
    scale = DEF_SCALE;
    seed = DEF_SEED;
    width = DEF_WIDTH;
    height = DEF_HEIGHT;
    heightScale = DEF_HEIGHT_SCALE;
    sampleSpacing = DEF_SAMPLE_SPACING;
  }
  
  public SimplexNoiseGen(SimplexNoiseGen ng) {
    octaves = ng.getOctaves();
    roughness = ng.getRoughness();
    scale = ng.getScale();
    seed = ng.getSeed();
    width = ng.getWidth();
    height = ng.getHeight();
    heightScale = ng.getHeightScale();
    sampleSpacing = ng.getSampleSpacing();
  }
  
  
  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(octaves, "octaves", -1);
    capsule.write(roughness, "roughness", -1);
    capsule.write(scale, "scale", -1);
    capsule.write(seed, "seed", -1);
    capsule.write(width, "width", -1);
    capsule.write(height, "height", -1);
    capsule.write(heightScale, "heightScale", -1);
    capsule.write(sampleSpacing, "sampleSpacing", -1);
    
  }
  
  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    octaves = capsule.readInt("octaves", DEF_OCTAVES);
    roughness = capsule.readDouble("roughness", DEF_ROUGHNESS);
    scale = capsule.readDouble("scale",DEF_SCALE);
    seed = capsule.readLong("seed", DEF_SEED);
    width = capsule.readInt("width", DEF_WIDTH);
    height=capsule.readInt("height", DEF_HEIGHT);
    heightScale = capsule.readFloat("heightScale", DEF_HEIGHT_SCALE);
    sampleSpacing = capsule.readDouble("sampleSpacing", DEF_SAMPLE_SPACING);
  }
  
  public float[] getOrUpdateHeightMap() {
    if(heightMap != null && genHash == hashCode()) {
      return heightMap;
    }
    
    heightMap = generateOctavedSimplexNoise(width, height);
    HeightMapUtil.normalise(heightMap);
    if(heightScale != 1) {
      HeightMapUtil.scaleHeights(heightMap, heightScale);
    }
    genHash = hashCode();
    
    return heightMap;
  }
  
  private float[] generateOctavedSimplexNoise(int width, int height) {
    float[] totalNoise = new float[width * height];
    double layerFrequency = scale;
    double layerWeight = 1;
    
    SimplexNoise.setSeed(seed);

    // Summing up all octaves, the whole expression makes up a weighted average
    // computation where the noise with the lowest frequencies have the least effect

    int index = 0;
    for (int octave = 0; octave < octaves; octave++) {
      // Calculate single layer/octave of simplex noise, then add it to total noise
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          totalNoise[index] += SimplexNoise.noise(x * sampleSpacing * layerFrequency, y * sampleSpacing * layerFrequency) * layerWeight;
          index++;
        }
      }

      // Increase variables with each incrementing octave
      layerFrequency *= 2;
      layerWeight *= roughness;
      index = 0;
      
    }
    return totalNoise;
  }
  
  
  public int getOctaves() {
    return octaves;
  }

  public void setOctaves(int octaves) {
    this.octaves = octaves;
  }

  public double getRoughness() {
    return roughness;
  }

  public void setRoughness(double roughness) {
    this.roughness = roughness;
  }

  public double getScale() {
    return scale;
  }

  public void setScale(double scale) {
    this.scale = scale;
  }

  public long getSeed() {
    return seed;
  }

  public void setSeed(long seed) {
    this.seed = seed;
  }
  
  public void setSize(int size) {
    setWidth(size);
    setHeight(size);
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public float getHeightScale() {
    return heightScale;
  }

  public void setHeightScale(float heightScale) {
    this.heightScale = heightScale;
  }

  public double getSampleSpacing() {
    return sampleSpacing;
  }

  public void setSampleSpacing(double sampleSpacing) {
    this.sampleSpacing = sampleSpacing;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + height;
    result = prime * result + Float.floatToIntBits(heightScale);
    result = prime * result + octaves;
    long temp;
    temp = Double.doubleToLongBits(roughness);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(sampleSpacing);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(scale);
    result = prime * result + (int) (temp ^ (temp >>> 32));
    result = prime * result + (int) (seed ^ (seed >>> 32));
    result = prime * result + width;
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    SimplexNoiseGen other = (SimplexNoiseGen) obj;
    if (height != other.height)
      return false;
    if (Float.floatToIntBits(heightScale) != Float.floatToIntBits(other.heightScale))
      return false;
    if (octaves != other.octaves)
      return false;
    if (Double.doubleToLongBits(roughness) != Double.doubleToLongBits(other.roughness))
      return false;
    if (Double.doubleToLongBits(sampleSpacing) != Double.doubleToLongBits(other.sampleSpacing))
      return false;
    if (Double.doubleToLongBits(scale) != Double.doubleToLongBits(other.scale))
      return false;
    if (seed != other.seed)
      return false;
    if (width != other.width)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "SimplexNoiseGen [octaves=" + octaves + ", roughness=" + roughness + ", scale=" + scale + ", seed=" + seed + ", width=" + width + ", height="
        + height + ", heightScale=" + heightScale + ", sampleSpacing=" + sampleSpacing + "]";
  }
  
  

}
