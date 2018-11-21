package gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import com.jme3.math.FastMath;

public class SplineElevationRamp extends ElevationRamp {

  private PolynomialSplineFunction func;
  
  private List<Vector2d> cps;
  
  public SplineElevationRamp() {
    setControlPoints(Collections.singletonList(new Vector2d(0.5,0.5)));
  }
  
  public List<Vector2d> getControlPoints() {
    return cps;
  }
  
  @Override
  public float applyRamp(float inVal) {
    if(func != null) {
      if(func.isValidPoint(inVal)) {
        return FastMath.clamp((float)func.value(inVal), 0, 1);
      }
    }
    return inVal;
  }
  
  public boolean isValidPoint(double rat) {
     if(func == null) {
       return false;
     }
    return func.isValidPoint(rat);
  }
  
  public boolean setControlPoints(List<Vector2d> controlPoints) {
    PolynomialSplineFunction f = getInterpFunc(controlPoints);
    if(f == null) {
      return false;
    }
    func = f;
    cps = controlPoints;
    return true;
  }
  
  private PolynomialSplineFunction getInterpFunc(List<Vector2d> controlPoints) {
    List<Vector2d> cps = new ArrayList<>(controlPoints.size() + 2);
    cps.add(new Vector2d(0.0, 0.0));
    cps.addAll(controlPoints);
    cps.add(new Vector2d(1.0, 1.0));

    double[] cpX = new double[cps.size()];
    double[] cpY = new double[cps.size()];
    for (int i = 0; i < cps.size(); i++) {
      Vector2d cp = cps.get(i);
      cpX[i] = cp.x;
      cpY[i] = cp.y;
    }
    SplineInterpolator si = new SplineInterpolator();
    PolynomialSplineFunction interpa = null;
    try {
      interpa = si.interpolate(cpX, cpY);
    } catch (Exception e) {
      // return false;
    }
    return interpa;
  }

  @Override
  public String toString() {
    return "SplineElevationRamp [cps=" + Arrays.toString(cps.toArray()) + "]";
  }

  
  

}
