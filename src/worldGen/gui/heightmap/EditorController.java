package worldGen.gui.heightmap;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.RawInputListener;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.terrain.geomipmap.TerrainQuad;

import worldGen.render.WorldRenderer;

public class EditorController {

  private WorldRenderer world;
  private CanvasListener listener;
  private boolean enabled;
  private boolean doStuff;

  private Timer timer;
  
  public EditorController(WorldRenderer world) {
    this.world = world;
    listener = new CanvasListener();
    timer = new Timer(true);
  }

  public void registerWithInput() {
    InputManager inputManager = world.getInputManager();
    inputManager .addRawInputListener(listener);
  }

  public void unregisterInput() {
    world.getInputManager().removeRawInputListener(listener);
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void setIncHeight(boolean doStuff) {
    this.doStuff = doStuff;
  }

  private Vector3f getWorldIntersection(int x, int y) {

    Camera cam = world.getCamera();

    Vector3f origin = cam.getWorldCoordinates(new Vector2f(x, y), 0.0f);
    Vector3f direction = cam.getWorldCoordinates(new Vector2f(x, y), 0.3f);
    direction.subtractLocal(origin).normalizeLocal();

    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    if (terrain == null) {
      return null;
    }

    Ray ray = new Ray(origin, direction);
    CollisionResults results = new CollisionResults();
    int numCollisions = terrain.collideWith(ray, results);
    if (numCollisions > 0) {
      CollisionResult hit = results.getClosestCollision();
      return hit.getContactPoint();
    }
    return null;
  }

  private void modifyTerrain(Vector3f intersect, int dir) {
    
    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    
    float radius = terrain.getTotalSize() / 10;
    float height = dir * world.getTerrainGenerator().getHeightScale() / -500;
    adjustHeight(intersect, radius, height);
    System.out.println("EditorController.modifyTerrain: rad=" + radius + " height=" + height);
  }

  private void adjustHeight(Vector3f loc, float radius, float height) {

    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    if (terrain == null) {
      return;
    }

    // offset it by radius because in the loop we iterate through 2 radii
    int radiusStepsX = (int) (radius / terrain.getLocalScale().x);
    int radiusStepsZ = (int) (radius / terrain.getLocalScale().z);

    float xStepAmount = terrain.getLocalScale().x;
    float zStepAmount = terrain.getLocalScale().z;
    long start = System.currentTimeMillis();
    List<Vector2f> locs = new ArrayList<>();
    List<Float> heights = new ArrayList<>();

    for (int z = -radiusStepsZ; z < radiusStepsZ; z++) {
      for (int x = -radiusStepsX; x < radiusStepsX; x++) {

        float locX = loc.x + (x * xStepAmount);
        float locZ = loc.z + (z * zStepAmount);

        if (isInRadius(locX - loc.x, locZ - loc.z, radius)) {
          // see if it is in the radius of the tool
          float h = calculateHeight(radius, height, locX - loc.x, locZ - loc.z);
          locs.add(new Vector2f(locX, locZ));
          heights.add(h);
        }
      }
    }

    terrain.adjustHeight(locs, heights);
    // System.out.println("Modified "+locs.size()+" points, took: " +
    // (System.currentTimeMillis() - start)+" ms");
    terrain.updateModelBound();
  }

  private boolean isInRadius(float x, float y, float radius) {
    Vector2f point = new Vector2f(x, y);
    // return true if the distance is less than equal to the radius
    return point.length() <= radius;
  }

  private float calculateHeight(float radius, float heightFactor, float x, float z) {
    // find percentage for each 'unit' in radius
    Vector2f point = new Vector2f(x, z);
    float val = point.length() / radius;
    val = 1 - val;
    if (val <= 0) {
      val = 0;
    }
    return heightFactor * val;
  }

  private class CanvasListener implements RawInputListener {

    private TimerTask currentTask;
    private int mouseX;
    private int mouseY;
    
    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
      if (!enabled) {
        return;
      }
      mouseX = evt.getX();
      mouseY = evt.getY();
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
      if (!enabled) {
        return;
      }
      mouseX = evt.getX();
      mouseY = evt.getY();
      
      if(!doStuff) {
        return;
      }
      
      if(evt.isReleased()) {
        if(currentTask != null) {
          currentTask.cancel();
        }
        return;
      }
      
      final int dir = evt.getButtonIndex() == 0 ? 1 : -1;
      currentTask = new TimerTask() {
        
        @Override
        public void run() {
          if(!enabled) {
            cancel();
            return;
          }
          //TODO: not thread safe
          Vector3f intersect = getWorldIntersection(mouseX, mouseY);
          modifyTerrain(intersect, dir);
          System.out.println("EditorController.CanvasListener.onMouseButtonEvent(...).new TimerTask() {...}.onMouseButtonEvent: xxxxxx");
        }
      };
      timer.schedule(currentTask, 0, 100);
      
      
    }

    @Override
    public void beginInput() {
    }

    @Override
    public void endInput() {
    }

    @Override
    public void onJoyAxisEvent(JoyAxisEvent evt) {
    }

    @Override
    public void onJoyButtonEvent(JoyButtonEvent evt) {
    }

    @Override
    public void onKeyEvent(KeyInputEvent evt) {
    }

    @Override
    public void onTouchEvent(TouchEvent evt) {
    }

  }

}
