package gen;

import noise.SimplexNoiseGenerator;

public class TerrainGen {

  private int octaves = 12;
  private double roughness = 0.6;
  private double scale = 0.003;
  private long seed = 1;

  
  public float[] generateSimplexHeightmap(int width, int height, float heightScale) {
    float[] res = generateSimplexHeightmap(width, height);
    HeightMapUtil.scale(res, heightScale);
    return res;
  }

  public float[] generateSimplexHeightmap(int width, int height) {
    SimplexNoiseGenerator gen = new SimplexNoiseGenerator(octaves, roughness, scale, seed);
    float[] hm = gen.generateOctavedSimplexNoise(width, height);
    HeightMapUtil.normalise(hm);
    return hm;
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

}
