package worldGen.gui.heightmap;

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

import worldGen.gen.HeightMapUtil;
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
    inputManager.addRawInputListener(listener);
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

  private void modifyTerrain(Vector3f intersect, int dir) {
    TerrainQuad terrain = world.getHeightMapEditor().getTerrain();
    float radius = terrain.getTotalSize() / 10;
    float height = dir * world.getTerrainGenerator().getHeightScale() / -500;
    HeightMapUtil.adjustHeight(terrain, intersect, radius, height);
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
      
      final int dir = evt.getButtonIndex() == 0 ? -1 : 1;
      currentTask = new TimerTask() {
        @Override
        public void run() {
          if(!enabled) {
            cancel();
            return;
          }
          world.enqueue(new Runnable() {
            @Override
            public void run() {
              Vector3f intersect = getWorldIntersection(mouseX, mouseY);
              modifyTerrain(intersect, dir);
            }
          });
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
