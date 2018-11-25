package worldGen.render;

import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.RawHeightMap;

import worldGen.gen.HeightMapProvider;
import worldGen.gen.HeightMapUtil;

public class HeightMapEditor {

  private WorldRenderer world;
  private TerrainQuad terrain;
  
  private RawHeightMap heightmap;
  
  public HeightMapEditor() {
  }
  
  public void init(WorldRenderer world) {
    this.world = world;
    TerrainGenerator tGen = world.getTerrainGenerator();
    int size = tGen.getSize();
    
//    String hms = tGen.getBaseHeightMapSource();
//
//    float[] heightData = ImageHeightmapLoader.loadGrayScaleData(hms, size, 1, true);
    
    HeightMapProvider hm = tGen.getBaseHeightMap();
    float[] heightData = hm.getOrUpdateHeightMap(tGen.getSize(), true);
    HeightMapUtil.scale(heightData, tGen.getRenderedHeightScale());
    
    
    heightmap = new RawHeightMap(heightData);
    
    int patchSize = size + 1;
    terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());
    
    terrain.setMaterial(tGen.getTerrainMaterial());
    
  }
  
  public void attach() {
    if(world == null || terrain == null) {
      return;
    }
    world.getRootNode().attachChild(terrain);
  }
  
  public void detatch() {
    if(world == null || terrain == null) {
      return;
    }
    terrain.removeFromParent();
    
    
    //TODO: Wrong place ?s
    float[] heightData = terrain.getHeightMap();
    float[] result = new float[heightData.length];
    System.arraycopy(heightData, 0, result, 0, heightData.length);
    HeightMapUtil.normalise(result);
    world.getTerrainGenerator().getBaseHeightMap().setHeightData(result);
    System.out.println("HeightMapEditor.detatch: dataSize=" + terrain.getTotalSize() + " terrainSize=" + world.getTerrainGenerator().getSize());
    
  }

  public TerrainQuad getTerrain() {
    return terrain;
  }
  
}
