package noise;

public class TerrainGen {

  private int octaves = 12;
  private double roughness = 0.6;
  private double scale = 0.003;
  private long seed = 1;

  
  public float[] generateHeightmap(int width, int height, float heightScale) {
    float[] res = generateHeightmap(width, height);
    for(int i=0;i<res.length;i++) {
      res[i] *= heightScale;
    }
    return res;
  }
  
  public float[] generateHeightmap(int width, int height) {
    
    SimplexNoiseGenerator gen = new SimplexNoiseGenerator(octaves, roughness, scale, seed);
    float[] hm = gen.generateOctavedSimplexNoise(width, height);

    normalise(hm);

    return hm;
  }

  private void normalise(float[] hm) {
    float[] minMax = getMinMax(hm);
    // System.out.println("TerrainGen.genSimplex: minVal=" + minMax[0] + " maxVal="
    // + minMax[1]);

    for (int i = 0; i < hm.length; i++) {
        hm[i] = normalise(hm[i], minMax[0], minMax[1]);
    }
  }

  private float normalise(float val, float min, float max) {
    float normalised = val + -min;
    max += -min;
    normalised /= max;
    return normalised;
  }

  private float[] getMinMax(float[] hm) {
    float[] res = new float[] { Float.MAX_VALUE, -Float.MAX_VALUE };
    for (int i = 0; i < hm.length; i++) {
      float val = hm[i];
      res[0] = val < res[0] ? val : res[0];
      res[1] = val > res[1] ? val : res[1];
    }

    return res;
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
