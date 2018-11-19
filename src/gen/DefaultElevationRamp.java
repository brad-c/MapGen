package gen;

public class DefaultElevationRamp extends ElevationRamp {

  private float b;
  private float maxVal;
  
  public DefaultElevationRamp() {
    //func = y = b^x - 1
    setB(100);
  }
  
  public void setB(float b) {
    this.b = b;
    maxVal = applyFunc(1);
  }

  private float applyFunc(float in) {
    return (float)Math.pow(b, in) - 1;
  }

  @Override
  public float applyRamp(float inVal) {
    float res = applyFunc(inVal);
    res = HeightMapUtil.normalise(res, 0, maxVal);
    return res;
  }

  
  
}
