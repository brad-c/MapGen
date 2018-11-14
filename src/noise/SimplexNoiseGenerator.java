package noise;
public class SimplexNoiseGenerator {

  private int octaves;
  private double roughness;
  private double scale;
  private long seed;

  public SimplexNoiseGenerator(int octaves, double roughness, double scale, long seed) {
    this.octaves = octaves; // Number of Layers combined together to get a natural looking surface
    this.roughness = roughness; // Increasing the of the range between -1 and 1, causing higher values eg more
                  // rough terrain
    this.scale = scale; // Overall scaling of the terrain
    this.seed = seed;
  }
  
  
  
  public float[] generateOctavedSimplexNoise(int width, int height) {
    float[] totalNoise = new float[width * height];
    double layerFrequency = scale;
    double layerWeight = 1;
    double weightSum = 0;
    
    SimplexNoise.setSeed(seed);

    // Summing up all octaves, the whole expression makes up a weighted average
    // computation where the noise with the lowest frequencies have the least effect

    int index = 0;
    for (int octave = 0; octave < octaves; octave++) {
      // Calculate single layer/octave of simplex noise, then add it to total noise
      for (int x = 0; x < width; x++) {
        for (int y = 0; y < height; y++) {
          totalNoise[index] += SimplexNoise.noise(x * layerFrequency, y * layerFrequency) * layerWeight;
          index++;
        }
      }

      // Increase variables with each incrementing octave
      layerFrequency *= 2;
      weightSum += layerWeight;
      layerWeight *= roughness;

      index = 0;
      
    }
    return totalNoise;
  }
  
}
