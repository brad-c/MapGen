package render;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.renderer.Camera;

public class CameraState implements Savable {


  public static CameraState saveState(Camera cam) {
    try {
      CameraState res = new CameraState(cam);
      return res;
    } catch(Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public static void applyState(Camera to, CameraState from) {
    if (to == null || from == null) {
      return;
    }
    to.copyFrom(from.camera);
    to.onFrustumChange();
    to.onViewPortChange();
    to.onFrameChange();
  }

  private Camera camera;

  public CameraState() {
  }

  public CameraState(Camera cam) {
    //Have to call this constructor to init all the values
    //or we get a NPE in copy from
    this.camera = new Camera(cam.getWidth(), cam.getHeight());
    camera.copyFrom(cam);
    camera.onFrustumChange();
    camera.onViewPortChange();
    camera.onFrameChange();
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(camera, "camera", null);
    capsule.write(camera.isParallelProjection(), "isParallelProjection", false);

  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    camera = (Camera)capsule.readSavable("camera", null);
    camera.setParallelProjection(capsule.readBoolean("isParallelProjection", false));
  }

}
