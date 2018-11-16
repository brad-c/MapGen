package render;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.export.binary.BinaryImporter;
import com.jme3.renderer.Camera;

public class CameraStateIO {

  public static CameraState saveState(Camera cam) {
    
    BinaryExporter be = new BinaryExporter();
    ByteArrayOutputStream os = new ByteArrayOutputStream();

    try {
      be.save(cam, os);
      CameraState res = new CameraState(os.toByteArray(), cam.isParallelProjection());
      return res;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
    
  }
  
  public static void applyState(Camera cam, CameraState data) {
    if(cam == null || data == null) {
      return;
    }
    
    BinaryImporter bi = new BinaryImporter();
    try {
      Camera cam2 = (Camera)bi.load(data.data);
      cam2.setParallelProjection(data.isPara);
      cam.copyFrom(cam2);
      
      cam.onFrustumChange();
      cam.onViewPortChange();
      cam.onFrameChange();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  public static class CameraState {
    
    private byte[] data;
    boolean isPara;
    
    public CameraState(byte[] data, boolean isPara) {
      super();
      this.data = data;
      this.isPara = isPara;
    }
    
    
  }
  
}
