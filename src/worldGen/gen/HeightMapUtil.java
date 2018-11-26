package worldGen.gen;

import java.util.ArrayList;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;

public class HeightMapUtil {

  public static void scaleHeights(float[] hm, float heightScale) {
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
  

  public static void normalise(float[] hm, float maxMin, float minMax) {
    float[] minAndMax = getMinMax(hm);
    if(minAndMax[0] > maxMin) {
      minAndMax[0] = maxMin;
    }
    if(minAndMax[1] < minMax) {
      minAndMax[1] = minMax;
    }
    for (int i = 0; i < hm.length; i++) {
      hm[i] = normalise(hm[i], minAndMax[0], minAndMax[1]);
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
  
  //TODO: Better quality scaling
  public static float[] scale(float[] heightData, int size, int newSize) {
    float[] result = new float[newSize * newSize];
    
    int index = 0;
    for(int x=0;x<newSize;x++) {
      for(int y=0;y<newSize;y++) {
        result[index] = getHeight(heightData, size, (float)x/(newSize - 1), (float)y/(newSize - 1));
        index++;
      }
    }
    return result;
  }
  
  
  private static float getHeight(float[] heightData, int size, float xRat, float yRat) {
    int x = (int)(xRat * (size - 1));
    int y = (int)(yRat * (size - 1));
    int index = (y * size) + x;
    return heightData[index];
  }


  /** ------------------ From https://github.com/jMonkeyEngine/jmonkeyengine/blob/master/jme3-examples/src/main/java/jme3test/terrain/TerrainTestModifyHeight.java */
  public static void adjustHeight(TerrainQuad terrain, Vector3f loc, float radius, float heightAdjust, float minHeight, float maxHeight) {

    // offset it by radius because in the loop we iterate through 2 radii
    int radiusStepsX = (int) (radius / terrain.getLocalScale().x);
    int radiusStepsZ = (int) (radius / terrain.getLocalScale().z);

    float xStepAmount = terrain.getLocalScale().x;
    float zStepAmount = terrain.getLocalScale().z;

    List<Vector2f> locs = new ArrayList<>();
    List<Float> heights = new ArrayList<>();

    for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
      for (int x = -radiusStepsX; x < radiusStepsX; x++) {

        float locX = loc.x + (x * xStepAmount);
        float locZ = loc.z + (z * zStepAmount);

        if (isInRadius(locX - loc.x, locZ - loc.z, radius)) {
          // see if it is in the radius of the tool
          float h = calculateHeight(radius, heightAdjust, locX - loc.x, locZ - loc.z);

          //TODO: This is a really crappy hack to cap the values
          float newHeight = terrain.getHeight(new Vector2f(locX, locZ)) + h;
          if(newHeight < minHeight || newHeight > maxHeight) {
            h = 0;
          }
          
          locs.add(new Vector2f(locX, locZ));
          heights.add(h);
        }
      }
    }

    terrain.adjustHeight(locs, heights);

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
