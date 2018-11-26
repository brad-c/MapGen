package worldGen.gui.heightmap;

import com.jme3.input.event.MouseButtonEvent;
import com.jme3.math.Vector3f;

import worldGen.render.WorldRenderer;

public interface EditOperation {

  void doOperation(MouseButtonEvent evt, WorldRenderer world, Vector3f intersect);

}
