package worldGen.render;

import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.RawHeightMap;

import crap.ImageHeightmapLoader;
import worldGen.gen.HeightMapUtil;

public class HeightMapEditor {

  private WorldRenderer world;
  private TerrainQuad terrain;
  
  public HeightMapEditor() {
  }
  
  public void init(WorldRenderer world) {
    this.world = world;
    TerrainGenerator tGen = world.getTerrainGenerator();
    int size = tGen.getSize();
    
    String hms = tGen.getBaseHeightMapSource();
    
    float[] heightData = ImageHeightmapLoader.loadGrayScaleData(hms, size, 1, true);
    
    HeightMapUtil.scale(heightData, tGen.getRenderedHeightScale());
    
    AbstractHeightMap heightmap;
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
  }

  public TerrainQuad getTerrain() {
    return terrain;
  }
  
}
