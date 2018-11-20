package gen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.vecmath.Vector2d;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

public class SplineElevationRamp extends ElevationRamp {

  private PolynomialSplineFunction func;
  
  
  public SplineElevationRamp() {
    func = getInterpFunc(Collections.singletonList(new Vector2d(0.5,0.5)));
  }
  
  @Override
  public float applyRamp(float inVal) {
    if(func != null) {
      if(func.isValidPoint(inVal)) {
        return (float)func.value(inVal);
      }
    }
    return inVal;
  }
  
  public boolean setControlPoints(List<Vector2d> controlPoints) {
    PolynomialSplineFunction f = getInterpFunc(controlPoints);
    if(f == null) {
      return false;
    }
    func = f;
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

}
