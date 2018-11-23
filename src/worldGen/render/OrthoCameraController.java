package worldGen.render;

import java.awt.Canvas;

import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.RawInputListener;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.event.JoyAxisEvent;
import com.jme3.input.event.JoyButtonEvent;
import com.jme3.input.event.KeyInputEvent;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.input.event.TouchEvent;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.system.JmeCanvasContext;

public class OrthoCameraController implements AnalogListener, ActionListener {

  private static final String PAN_LEFT = "PAN_LEFT";
  private static final String PAN_RIGHT = "PAN_RIGHT";
  private static final String PAN_UP = "PAN_UP";
  private static final String PAN_DOWN = "PAN_DOWN";

  private static final String ZOOM_IN = "ZOOM_IN";
  private static final String ZOOM_OUT = "ZOOM_OUT";

  private static final Vector2f LEFT = new Vector2f(1, 0);
  private static final Vector2f RIGHT = new Vector2f(-1, 0);
  private static final Vector2f UP = new Vector2f(0, -1);
  private static final Vector2f DOWN = new Vector2f(0, 1);

  private static String[] mappings = new String[] { PAN_LEFT, PAN_RIGHT, PAN_UP, PAN_DOWN, ZOOM_IN, ZOOM_OUT };

  private Camera camera;
  private boolean enabled = true;
  private InputManager inputManager;

  private Vector2f panOffset = new Vector2f();
  private float zoomScale = 512;

  private WorldRenderer terrainRenderer;

  public OrthoCameraController(WorldRenderer terrainRenderer) {
    this.camera = terrainRenderer.getCamera();
    this.terrainRenderer = terrainRenderer;
    zoomScale = terrainRenderer.getTerrainGenerator().getSize();
  }

  public void registerWithInput(InputManager inputManager) {

    this.inputManager = inputManager;

    inputManager.addMapping(PAN_LEFT, new KeyTrigger(KeyInput.KEY_LEFT), new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping(PAN_RIGHT, new KeyTrigger(KeyInput.KEY_RIGHT), new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping(PAN_UP, new KeyTrigger(KeyInput.KEY_UP), new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping(PAN_DOWN, new KeyTrigger(KeyInput.KEY_DOWN), new KeyTrigger(KeyInput.KEY_S));

    inputManager.addMapping(ZOOM_IN, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false), new KeyTrigger(KeyInput.KEY_Q));
    inputManager.addMapping(ZOOM_OUT, new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true), new KeyTrigger(KeyInput.KEY_Z));

    inputManager.addRawInputListener(new MyMouseListener());

    inputManager.addListener(this, mappings);
    inputManager.setCursorVisible(true);

  }

  public void setEnabled(boolean enable) {
    if (enabled && !enable) {
      if (inputManager != null) {
        inputManager.setCursorVisible(true);
      }
    }
    enabled = enable;

    if (enabled) {
      updateFrustrum();
    }

  }

  public void updateFrustrum() {
    JmeCanvasContext context = (JmeCanvasContext) terrainRenderer.getContext();
    Canvas canvas = context.getCanvas();

    float aspectRatio = (float) canvas.getWidth() / canvas.getHeight();
    float w;
    float h;
    if (canvas.getWidth() > canvas.getHeight()) {
      h = zoomScale;
      w = zoomScale * aspectRatio;
    } else {
      w = zoomScale;
      h = zoomScale / aspectRatio;
    }
    camera.setFrustum(0.1f, 1000, -w , w , h , -h );
    camera.setFrame(new Vector3f(-panOffset.x, 1000, panOffset.y), new Vector3f(1, 0, 0), new Vector3f(0, 0, 1), new Vector3f(0, -1, 0));
    camera.setParallelProjection(true);
  }

  public void unregisterInput() {
    if (inputManager == null) {
      return;
    }

    for (String s : mappings) {
      if (inputManager.hasMapping(s)) {
        inputManager.deleteMapping(s);
      }
    }

    inputManager.removeListener(this);
    inputManager.setCursorVisible(true);
  }

  @Override
  public void onAction(String name, boolean isPressed, float tpf) {
    if (!enabled) {
      return;
    }
  }

  @Override
  public void onAnalog(String name, float value, float tpf) {
    if (!enabled) {
      return;
    }

    if (name.equals(PAN_UP)) {
      moveCamera(value, UP);
    } else if (name.equals(PAN_DOWN)) {
      moveCamera(value, DOWN);
    } else if (name.equals(PAN_LEFT)) {
      moveCamera(value, LEFT);
    } else if (name.equals(PAN_RIGHT)) {
      moveCamera(value, RIGHT);
    } else if (name.equals(ZOOM_IN)) {
      zoomCamera(value);
    } else if (name.equals(ZOOM_OUT)) {
      zoomCamera(-value);
    }
  }

  private void zoomCamera(float value) {
    float inc = zoomScale / 10f;
    zoomScale -= inc * value;
    updateFrustrum();

  }

  private void moveCamera(float value, Vector2f dir) {
    float inc = zoomScale;
    panOffset.x += dir.x * inc * value;
    panOffset.y += dir.y * inc * value;
    updateFrustrum();
  }

  private class MyMouseListener implements RawInputListener {

    private boolean isDrag = false;

    @Override
    public void onMouseMotionEvent(MouseMotionEvent evt) {
      if(!enabled) {
        return;
      }
      if (isDrag) {
        int dx = evt.getDX();
        int dy = evt.getDY();
        JmeCanvasContext context = (JmeCanvasContext) terrainRenderer.getContext();
        Canvas canvas = context.getCanvas();
        int pixels = canvas.getWidth() > canvas.getHeight() ? canvas.getHeight() : canvas.getWidth();
        float unitsPerPixel = zoomScale / pixels;

        panOffset.x -= (dx * unitsPerPixel * 2);
        panOffset.y -= (dy * unitsPerPixel * 2);
        updateFrustrum();
      }
    }

    @Override
    public void onMouseButtonEvent(MouseButtonEvent evt) {
      if(!enabled) {
        return;
      }
      
      if (evt.getButtonIndex() != 0) {
        return;
      }
      if (evt.isPressed()) {
        isDrag = true;
      } else if (evt.isReleased()) {
        isDrag = false;
      }
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
