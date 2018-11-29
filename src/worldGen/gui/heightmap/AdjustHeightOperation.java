package worldGen.gui.heightmap;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.debug.Arrow;
import com.jme3.scene.shape.Sphere;
import com.jme3.terrain.geomipmap.TerrainQuad;

import worldGen.gen.HeightMapUtil;
import worldGen.render.WorldRenderer;

public class AdjustHeightOperation implements EditOperation {

  private float radiusRatio = 0.5f;
  private float speedRatio = 0.5f;

  private Geometry marker;
  private Geometry markerNormal;

  private WorldRenderer world;

  public AdjustHeightOperation(WorldRenderer world) {
    this.world = world;
  }

  @Override
  public void setEnabled(boolean enabled) {
    if (marker == null) {
      return;
    }
    if (!enabled) {
      marker.removeFromParent();
      markerNormal.removeFromParent();
    } else {
      world.getRootNode().attachChild(marker);
      world.getRootNode().attachChild(markerNormal);
    }
  }

  @Override
  public void onMouseMotionEvent(MouseMotionEvent evt, WorldRenderer world, Vector3f intersect) {
    updateMarkerPos(intersect);
  }

  @Override
  public void doOperation(MouseButtonEvent evt, WorldRenderer world, Vector3f intersect) {
    int dir = evt.getButtonIndex() == 0 ? -1 : 1;
    modifyTerrain(world, intersect, dir);
    updateMarkerPos(intersect);
  }

  private void modifyTerrain(WorldRenderer world, Vector3f intersect, int dir) {
    if (intersect == null) {
      return;
    }
    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    float radius = getScaledRadius(terrain);
    float height = dir * world.getTerrainGenerator().getRenderedHeightScale() / -40;
    height *= speedRatio;
    HeightMapUtil.adjustHeight(terrain, intersect, radius, height, 0, world.getTerrainGenerator().getRenderedHeightScale());
  }

  public float getRadius() {
    return radiusRatio;
  }

  public void setRadius(float radius) {
    this.radiusRatio = radius;
    resetMarker();
  }

  private void resetMarker() {
    if (marker != null) {
      marker.removeFromParent();
      marker = null;
      markerNormal.removeFromParent();
      markerNormal = null;
    }
  }

  public float getSpeed() {
    return speedRatio;
  }

  public void setSpeed(float speed) {
    this.speedRatio = speed;
  }

  private float getScaledRadius(TerrainQuad terrain) {
    float radius = terrain.getTotalSize() / 4;
    radius *= radiusRatio;
    return radius;
  }

  public void updateMarkerPos(Vector3f intersection) {

    if (intersection == null) {
      resetMarker();
      return;
    }

    if (marker == null) {
      createMarker();
      world.getRootNode().attachChild(marker);
      world.getRootNode().attachChild(markerNormal);
    }

    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    if (terrain == null || intersection == null) {
      return;
    }
    marker.setLocalTranslation(new Vector3f(intersection.x, intersection.y, intersection.z));
    markerNormal.setLocalTranslation(new Vector3f(intersection.x, intersection.y, intersection.z));
    
    Vector3f normal = terrain.getNormal(new Vector2f(intersection.x, intersection.z));
    normal.multLocal(getScaledRadius(terrain));
    ((Arrow) markerNormal.getMesh()).setArrowExtent(normal);

  }

  private void createMarker() {
    // collision marker
    Sphere sphere = new Sphere(64, 64, getScaledRadius(world.getHeightMapEditor().getTerrain()));
    marker = new Geometry("Marker");
    marker.setMesh(sphere);

    Material mat = new Material(world.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", new ColorRGBA(1f, 1f, 0f, 0.3f));
    mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
    mat.getAdditionalRenderState().setDepthWrite(false);
    marker.setMaterial(mat);
    
    mat = new Material(world.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
    mat.setColor("Color", new ColorRGBA(1f, 0f, 0f, 1f));
    
    // surface normal marker
    Arrow arrow = new Arrow(new Vector3f(0, 1, 0));
    markerNormal = new Geometry("MarkerNormal");
    markerNormal.setMesh(arrow);
    markerNormal.setMaterial(mat);

  }

}
