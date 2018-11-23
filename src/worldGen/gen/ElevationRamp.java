package worldGen.gen;

import com.jme3.export.Savable;

public abstract class ElevationRamp implements Savable {
  
  public ElevationRamp() {
  }
  
  public float[] apply(float[] hm, float minToApplyTo, float maxToApplyTo) {
    
    float[] res = new float[hm.length];
    
    float range = maxToApplyTo - minToApplyTo;
    
    for(int i=0;i<res.length;i++) {
      float inVal = hm[i];
      if(inVal >= minToApplyTo && inVal <= maxToApplyTo) {
        
        //normalise inVal
        inVal = HeightMapUtil.normalise(inVal, minToApplyTo, maxToApplyTo);
        inVal = applyRamp(inVal);
        //put it back into range
        inVal = minToApplyTo + inVal * range;
        
        res[i] = inVal;
                
      } else {
        res[i] = inVal;
      }
    }
    
    return res;
  }

  public abstract float applyRamp(float inVal);
  
}
