package worldGen.gui.heightmap;

import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
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

  public EditorController(WorldRenderer world) {
    this.world = world;
    listener = new CanvasListener();
  }
  
  public void registerWithInput() {
    world.getInputManager().addRawInputListener(listener);
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
    if(terrain == null) {
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


    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
      if(!enabled) {
        return;
      }
      
      
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
      if(!enabled) {
        return;
      }
      if(!doStuff) {
        return;
      }
      Vector3f intersect = getWorldIntersection(evt.getX(), evt.getY());
      System.out.println("EditorController.CanvasListener.onMouseButtonEvent: " + intersect);
      
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
