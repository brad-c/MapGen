package gen;

import noise.SimplexNoise;

public class SimpleNoiseGen {

  private int octaves = 12;
  private double roughness = 0.6;
  private double scale = 0.003;
  private long seed = 8092038379555713298l;
  
  private int width = 512;
  private int height = 512;
  private float heightScale = 1;

  private double sampleSpacing = 1;

  private transient float[] heightMap = null;
  private transient int genHash = -1;
  
  public SimpleNoiseGen() {
  }
  
  public float[] getOrUpdateHeightMap() {
    if(heightMap != null && genHash == hashCode()) {
      return heightMap;
    }
    
    heightMap = generateOctavedSimplexNoise(width, height);
    HeightMapUtil.normalise(heightMap);
    if(heightScale != 1) {
      HeightMapUtil.scale(heightMap, heightScale);
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
    SimpleNoiseGen other = (SimpleNoiseGen) obj;
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

}
