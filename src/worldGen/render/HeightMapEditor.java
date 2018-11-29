package worldGen.render;

import com.jme3.scene.Node;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.RawHeightMap;

import worldGen.gen.HeightMapUtil;

public class HeightMapEditor {

  private WorldRenderer world;
  private TerrainQuad terrain;
  
  private int size;
  
  private Node rootNode;
  private float maxHeight;
  
  public HeightMapEditor() {
    rootNode = new Node();
  }
  
  public void init(WorldRenderer world) {
    this.world = world;
  }
  
  public void clearChanges() {
    terrain = null;
  }
  
  public void applyChanges() {
    if(world == null) {
      return;
    }
    doApply();
    
    if(world.getTerrainGenerator().getSize() != size) {
      //The size of the terrain has been changed so get a rescaled version to work on
      updateLocalTerrain();
    }
  }
  
  public void updateLocalTerrain() {

    rootNode.detachAllChildren();
    
    TerrainGenerator tGen = world.getTerrainGenerator();
    size = tGen.getSize();
    maxHeight = tGen.getRenderedHeightScale();
        
    float[] heightData = tGen.getBaseHeightMap().getOrUpdateHeightMap(size, true);
    HeightMapUtil.scaleHeights(heightData, maxHeight);
    RawHeightMap heightmap = new RawHeightMap(heightData);
      
    int patchSize = size + 1;
    terrain = new TerrainQuad("BaseHeightMap", patchSize, size + 1, heightmap.getHeightMap());
    terrain.setMaterial(tGen.getTerrainMaterial());
    
    rootNode.attachChild(terrain);
  }
  
  public void attach() {
    if(world == null) {
      return;
    }
    updateLocalTerrain();
    world.getRootNode().attachChild(rootNode);
  }
  
  public void detatch() {
    if(world == null) {
      return;
    }
    rootNode.removeFromParent();
    doApply();
    world.updateTerrain();
  }

  private void doApply() {
    if(terrain == null) {
      return;
    }
    
    float[] heightData = terrain.getHeightMap();
        
    
    float[] result = new float[size * size];
    
    //just chopping off last column and last row as
    //the terrain adds one for some reason
    int sourceIndex = 0 ;
    int targetIndex = 0 ;
    for(int x=0; x < size; x++) {
      for(int y=0; y < size; y++) {
        result[targetIndex] = heightData[sourceIndex];
        sourceIndex++;
        targetIndex++;
      }
      sourceIndex++;
    }
    HeightMapUtil.normalise(result, 0, maxHeight);
    
    world.getTerrainGenerator().getBaseHeightMap().setHeightData(result);
  }

  public TerrainQuad getTerrain() {
    return terrain;
  }


}
