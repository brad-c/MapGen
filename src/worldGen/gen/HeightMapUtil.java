package worldGen.gen;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;

public class HeightMapUtil {

  public static void scale(float[] hm, float heightScale) {
    for (int i = 0; i < hm.length; i++) {
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

  public static double normalise(double val, double min, double max) {
    double normalised = val + -min;
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

  public static double clamp(double val, double min, double max) {
    return (val < min) ? min : (val > max) ? max : val;
  }
  
  /** ------------------ From https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-examples/src/main/java/jme3test/terrain/TerrainTestModifyHeight.java */
  public static void adjustHeight(TerrainQuad terrain, Vector3f loc, float radius, float height) {

    // offset it by radius because in the loop we iterate through 2 radii
    int radiusStepsX = (int) (radius / terrain.getLocalScale().x);
    int radiusStepsZ = (int) (radius / terrain.getLocalScale().z);

    float xStepAmount = terrain.getLocalScale().x;
    float zStepAmount = terrain.getLocalScale().z;
    long start = System.currentTimeMillis();
    List<Vector2f> locs = new ArrayList<>();
    List<Float> heights = new ArrayList<>();

    for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
      for (int x = -radiusStepsX; x < radiusStepsX; x++) {

        float locX = loc.x + (x * xStepAmount);
        float locZ = loc.z + (z * zStepAmount);

        if (isInRadius(locX - loc.x, locZ - loc.z, radius)) {
          // see if it is in the radius of the tool
          float h = calculateHeight(radius, height, locX - loc.x, locZ - loc.z);
          locs.add(new Vector2f(locX, locZ));
          heights.add(h);
        }
      }
    }

    terrain.adjustHeight(locs, heights);
    // System.out.println("Modified "+locs.size()+" points, took: " +
    // (System.currentTimeMillis() - start)+" ms");
    terrain.updateModelBound();
  }

  private static boolean isInRadius(float x, float y, float radius) {
    Vector2f point = new Vector2f(x, y);
    // return true if the distance is less than equal to the radius
    return point.length() <= radius;
  }

  private static float calculateHeight(float radius, float heightFactor, float x, float z) {
    // find percentage for each 'unit' in radius
    Vector2f point = new Vector2f(x, z);
    float val = point.length() / radius;
    val = 1 - val;
    if (val <= 0) {
      val = 0;
    }
    return heightFactor * val;
  }
  
  /** ------------------ End From https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-examples/src/main/java/jme3test/terrain/TerrainTestModifyHeight.java */


}
