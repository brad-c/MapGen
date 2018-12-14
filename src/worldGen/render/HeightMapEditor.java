package worldGen.render;

import com.jme3.math.Vector3f;
import com.jme3.scene.Node;

import worldGen.gen.HeightMapUtil;

public class HeightMapEditor {

  private WorldRenderer world;
  private MyTerrainPatch tp;
  
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
    tp = null;
  }
  
  public void applyChanges() {
    if(world == null || tp == null) {
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
    
    tp = new MyTerrainPatch("a", size, new Vector3f(2,1,2), heightData, new Vector3f(-size,0,-size));
    tp.setMaterial(tGen.getTerrainMaterial());
    rootNode.attachChild(tp);

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
    tp = null;
  }

  private void doApply() {
    
    if(tp == null) {
      return;
    }
    
    //float[] heightData = terrain.getHeightMap();
    float[] heightData = tp.getHeightMap();
    float[] result = new float[size * size];

    //TODO: Array copy ok now??
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
  
  public MyTerrainPatch getTerrain() {
    return tp;
  }


}
