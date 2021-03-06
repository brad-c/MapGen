package worldGen.gui.heightmap;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.math.Vector3f;

import worldGen.render.WorldRenderer;

public interface EditOperation {

  void doOperation(MouseButtonEvent evt, WorldRenderer world, Vector3f intersect);

  void onMouseMotionEvent(MouseMotionEvent evt, WorldRenderer world, Vector3f intersect);

  void setEnabled(boolean enabled);

}
