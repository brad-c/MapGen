package viewer;

import vecmath.VecmathUtil;
import vecmath.Vector3d;

public class NormalGenerator {

  private Vector3d scale = new Vector3d(1,1,1);
  
  public Vector3d getNormal(double[][] hm, int x, int y) {
    
    Vector3d origin = getSample(hm, x, y);
    //get all the samples position relative to the origin
    Vector3d north = getSample(hm, x, y + 1);
    Vector3d south = getSample(hm, x, y - 1);
    Vector3d east = getSample(hm, x + 1, y);
    Vector3d west = getSample(hm, x - 1, y);
    
    Vector3d res = new Vector3d();
    res.add(getNormal(origin, north, west));
    res.add(getNormal(origin, west, south));
    res.add(getNormal(origin, south, east));
    res.add(getNormal(origin, east, north));
    
    
    res.normalize();
    return res;
    
  }
  
  public Vector3d getNormal(Vector3d p1, Vector3d p2, Vector3d p3) {
    Vector3d seg1 = new Vector3d(p2);
    seg1.sub(p1);
    
    Vector3d seg2 = new Vector3d(p3);
    seg2.sub(p1);
    
    Vector3d res = VecmathUtil.cross(seg1, seg2);
    res.normalize();
    return res;
    
  }

  private Vector3d getSample(Vector3d origin, double[][] hm, int x, int y, double xOffset, double yOffset) {
    double height = getHeight(hm, x, y);
    Vector3d res = new Vector3d(xOffset * scale.x, yOffset * scale.y, height * scale.z);
    if(origin != null) {
      res.sub(origin);
    }
    return res;
  }
  
  
  public Vector3d getNormal22(double[][] hm, int x, int y) {
    
    Vector3d origin = getSample(null, hm, x, y, 0, 0);
    //get all the samples position relative to the origin
    Vector3d north = getSample(origin, hm, x, y - 1, 0, 1);
    Vector3d south = getSample(origin,hm, x, y - 1, 0, -1);
    Vector3d east = getSample(origin,hm, x + 1, y, 1, 0);
    Vector3d west = getSample(origin, hm, x - 1, y, -1, 0);
    
    Vector3d res = new Vector3d();
    Vector3d normal = new Vector3d();
    
    //calc the normal for the 4 triangles
//    normal.cross(west, north);
//    res.add(normal);
    
    normal.cross(north, east);
    res.add(normal);
//
//    normal.cross(east, south);
//    res.add(normal);
//
//    normal.cross(south, west);
//    res.add(normal);
//
//    //average them
//    res.scale(0.25);
        
    res.normalize();
    return res;
  }
  
  private Vector3d getSample(double[][] hm, int x, int y) {
    double height = getHeight(hm, x, y);
    Vector3d res = new Vector3d(x * scale.x, y * scale.y, height * scale.z);
    return res;
  }

  private double getHeight(double[][] hm, int x, int y) {
    int sampleX;
    int sampleY;
    
    sampleX = x < 0 ? 0 : x;
    sampleX = x >= hm.length ? hm.length - 1 : sampleX;
   
    sampleY = y < 0 ? 0 : y;
    sampleY = y >= hm[0].length ? hm[0].length - 1 : sampleY;
        
    return hm[sampleX][sampleY];
  }
  
}
