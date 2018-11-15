package gen;

public class HeightMapUtil {

  
  public static void scale(float[] hm, float heightScale) {
    for(int i=0;i<hm.length;i++) {
      hm[i] *= heightScale;
    }
  }

  public static void normalise(float[] hm) {
    float[] minMax = getMinMax(hm);
    // System.out.println("TerrainGen.genSimplex: minVal=" + minMax[0] + " maxVal="
    // + minMax[1]);

    for (int i = 0; i < hm.length; i++) {
        hm[i] = normalise(hm[i], minMax[0], minMax[1]);
    }
  }

  public static float normalise(float val, float min, float max) {
    float normalised = val + -min;
    max += -min;
    normalised /= max;
    return normalised;
  }

  public static float[] getMinMax(float[] hm) {
    float[] res = new float[] { Float.MAX_VALUE, -Float.MAX_VALUE };
    for (int i = 0; i < hm.length; i++) {
      float val = hm[i];
      res[0] = val < res[0] ? val : res[0];
      res[1] = val > res[1] ? val : res[1];
    }

    return res;
  }

  
}
