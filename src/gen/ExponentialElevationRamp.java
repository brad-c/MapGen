package gen;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;

public class ExponentialElevationRamp extends ElevationRamp {

  public static final float DEF_B = 9.12f;
  
  private float b;
  private float maxVal;
  private boolean invert;
  
  public ExponentialElevationRamp() {
    this(false);
  }
  
  public ExponentialElevationRamp(boolean invert) {
    this(DEF_B, invert);
  }
  
  public ExponentialElevationRamp(float b) {
    this(b, false);
  }
  
  public ExponentialElevationRamp(float b, boolean invert) {
    this.b = b;
    this.invert = invert;
    maxVal = applyFunc(1);
  }

  public void setB(float b) {
    this.b = b;
    maxVal = applyFunc(1);
  }
  
  public float getB() {
    return b;
  }

  public boolean isInvert() {
    return invert;
  }

  public void setInvert(boolean invert) {
    this.invert = invert;
    maxVal = applyFunc(1);
  }

  private float applyFunc(float in) {
    if(invert) {
      return (float)Math.pow(b, -in) - 1;
    } else {
      return (float)Math.pow(b, in) - 1;
    }
  }

  @Override
  public float applyRamp(float inVal) {
    float res = applyFunc(inVal);
    res = HeightMapUtil.normalise(res, 0, maxVal);
    return res;
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule cap = ex.getCapsule(this);
    cap.write(b, "b", -1);
    cap.write(invert, "invert", false);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule cap = im.getCapsule(this);
    setB(cap.readFloat("b", DEF_B));
    setInvert(cap.readBoolean("invert", false));
  }

  @Override
  public String toString() {
    return "ExponentialElevationRamp [b=" + b + "]";
  }

}
