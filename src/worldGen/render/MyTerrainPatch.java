package worldGen.render;

import java.nio.FloatBuffer;
import java.util.List;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.terrain.geomipmap.TerrainPatch;
import com.jme3.terrain.geomipmap.TerrainQuad.LocationHeight;

public class MyTerrainPatch extends TerrainPatch {

  public MyTerrainPatch() {
    super();
  }

  public MyTerrainPatch(String name, int size, Vector3f stepScale, float[] heightMap, Vector3f origin, int totalSize, Vector2f offset, float offsetAmount) {
    super(name, size, stepScale, heightMap, origin, totalSize, offset, offsetAmount);
  }

  public MyTerrainPatch(String name, int size, Vector3f stepScale, float[] heightMap, Vector3f origin) {
    super(name, size, stepScale, heightMap, origin);
  }

  public MyTerrainPatch(String name, int size) {
    super(name, size);
  }

  public MyTerrainPatch(String name) {
    super(name);
  }

  @Override
  public Vector3f getMeshNormal(int x, int z) {
    return super.getMeshNormal(x, z);
  }

  public void adjustHeight(List<Vector2f> locs, List<Float> heights, float minHeight, float maxHeight) {
    // TODO Auto-generated method stub
    
  }
  
  protected void setHeight(List<LocationHeight> locationHeights, boolean overrideHeight) {

    final float[] heightArray = geomap.getHeightArray();
    final VertexBuffer vertexBuffer = mesh.getBuffer(Type.Position);
    final FloatBuffer floatBuffer = mesh.getFloatBuffer(Type.Position);

    for (LocationHeight lh : locationHeights) {

        if (lh.x < 0 || lh.z < 0 || lh.x >= size || lh.z >= size) {
            continue;
        }

        int idx = lh.z * size + lh.x;

        if (overrideHeight) {
            heightArray[idx] = lh.h;
        } else {
            float currentHeight = floatBuffer.get(idx * 3 + 1);
            heightArray[idx] = currentHeight + lh.h;
        }
    }

    floatBuffer.clear();
    geomap.writeVertexArray(floatBuffer, stepScale, false);
    vertexBuffer.setUpdateNeeded();
}

  
  
  
//  protected void setHeight(List<Vector2f> xz, List<Float> height, boolean overrideHeight) {
//    if (xz.size() != height.size())
//        throw new IllegalArgumentException("Both lists must be the same length!");
//
//    int halfSize = totalSize / 2;
//
//    List<LocationHeight> locations = new ArrayList<LocationHeight>();
//
//    // offset
//    for (int i=0; i<xz.size(); i++) {
//        int x = Math.round((xz.get(i).x / getWorldScale().x) + halfSize);
//        int z = Math.round((xz.get(i).y / getWorldScale().z) + halfSize);
//        if (!isInside(x, z))
//            continue;
//        locations.add(new LocationHeight(x,z,height.get(i)));
//    }
//
//    setHeight(locations, overrideHeight); // adjust height of the actual mesh
//
//    // signal that the normals need updating
//    for (int i=0; i<xz.size(); i++)
//        setNormalRecalcNeeded(xz.get(i) );
//  }
  
}
