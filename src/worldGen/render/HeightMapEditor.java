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
    
  }
  
  private void updateLocalTerrain() {
    TerrainGenerator tGen = world.getTerrainGenerator();
    HeightMapProvider hm = tGen.getBaseHeightMap();
        
    int size = tGen.getSize();
    float[] heightData = hm.getOrUpdateHeightMap(size, true);
    
    HeightMapUtil.scaleHeights(heightData, tGen.getRenderedHeightScale());
    heightmap = new RawHeightMap(heightData);
      
    int patchSize = size + 1;
    terrain = new TerrainQuad("my terrain", patchSize, size + 1, heightmap.getHeightMap());
    terrain.setMaterial(tGen.getTerrainMaterial());
  }
  
  public void attach() {
    if(world == null) {
      return;
    }
    updateLocalTerrain();
    world.getRootNode().attachChild(terrain);
  }
  
  public void detatch() {
    if(world == null || terrain == null) {
      return;
    }
    terrain.removeFromParent();
    
    
    float[] heightData = terrain.getHeightMap();
    
    //just chopping off last column and last row as
    //the terrain adds one for some reason
    int targetSize = (int)Math.sqrt(heightData.length - 1);
    float[] result = new float[targetSize * targetSize];
    
    int sourceIndex = 0 ;
    int targetIndex = 0 ;
    
    for(int x=0; x < targetSize; x++) {
      for(int y=0; y < targetSize; y++) {
        result[targetIndex] = heightData[sourceIndex];
        sourceIndex++;
        targetIndex++;
      }
      sourceIndex++;
    }
    TerrainGenerator tGen = world.getTerrainGenerator();
    HeightMapUtil.normalise(result, 0, tGen.getRenderedHeightScale());
    
    world.getTerrainGenerator().getBaseHeightMap().setHeightData(result);
    world.updateTerrain();
  }

  public TerrainQuad getTerrain() {
    return terrain;
  }

}
