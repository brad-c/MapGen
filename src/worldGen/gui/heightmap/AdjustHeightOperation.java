package worldGen.gui.heightmap;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector3f;
import com.jme3.terrain.geomipmap.TerrainQuad;

import worldGen.gen.HeightMapUtil;
import worldGen.render.WorldRenderer;

public class AdjustHeightOperation implements EditOperation {

  private float radiusRatio = 0.5f;
  private float speedRatio = 0.5f;
  
  public AdjustHeightOperation() {
    
  }
  
  @Override
  public void doOperation(MouseButtonEvent evt, WorldRenderer world, Vector3f intersect) {
    int dir = evt.getButtonIndex() == 0 ? -1 : 1;
    modifyTerrain(world, intersect, dir);
  }

  private void modifyTerrain(WorldRenderer world, Vector3f intersect, int dir) {
    if(intersect == null) {
      return;
    }
    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    float radius = terrain.getTotalSize() / 2;
    radius *= radiusRatio;
    float height = dir * world.getTerrainGenerator().getRenderedHeightScale() / -60;
    height *= speedRatio;
    HeightMapUtil.adjustHeight(terrain, intersect, radius, height, 0, world.getTerrainGenerator().getRenderedHeightScale());
  }

  public float getRadius() {
    return radiusRatio;
  }

  public void setRadius(float radius) {
    this.radiusRatio = radius;
  }

  public float getSpeed() {
    return speedRatio;
  }

  public void setSpeed(float speed) {
    this.speedRatio = speed;
  }
  
}
