package gen;

public class ExponentialElevationRamp extends ElevationRamp {

  private float b;
  private float maxVal;
  private boolean invert;
  
  public ExponentialElevationRamp() {
    this(false);
  }
  
  public ExponentialElevationRamp(boolean invert) {
    this(9.12f, invert);
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
  public String toString() {
    return "ExponentialElevationRamp [b=" + b + "]";
  }

}
