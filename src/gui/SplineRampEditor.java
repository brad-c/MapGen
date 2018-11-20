package gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import gen.HeightMapUtil;

public class SplineRampEditor {

  
  public static void showTestFrame() {
    SplineRampEditor ed = new SplineRampEditor();
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(ed.getEditor());
    f.pack();
    f.setVisible(true);
  }
  
  private JPanel rootPan;
  private SplineRenderPanel renPan;

  private List<Vector2d> controlPoints;

  public SplineRampEditor() {
    
    controlPoints = new ArrayList<>();
    
    renPan = new SplineRenderPanel();
    renPan.setPreferredSize(new Dimension(800, 800));
    renPan.setFocusable(true);

    InputHandler ih = new InputHandler();
    renPan.addMouseListener(ih);
    renPan.addMouseMotionListener(ih);
    renPan.addKeyListener(ih);

    rootPan = new JPanel(new BorderLayout());
    rootPan.add(renPan, BorderLayout.CENTER);
    
    
    controlPoints.add(new Vector2d(0.5,0.5));
    updateLine();
  }

  public Component getEditor() {
    return rootPan;
  }

  private PolynomialSplineFunction getInterpFunc() {
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

  private boolean updateLine() {

    Collections.sort(controlPoints, new Comparator<Vector2d>() {

      @Override
      public int compare(Vector2d o1, Vector2d o2) {
        return Double.compare(o1.x, o2.x);
      }
    });

    PolynomialSplineFunction interpa = getInterpFunc();
    if (interpa == null) {
      System.out.println("SplineRampEditor.updateLine: Could not get valid interpa");
      return false;
    }

    int numSamples = 1000;
    List<Vector2d> res = new ArrayList<>(numSamples);

    for (int i = 0; i < numSamples; i++) {
      double rat = (double) i / (numSamples - 1);
      if (interpa.isValidPoint(rat)) {
        Vector2d point = new Vector2d(rat, interpa.value(rat));
        point.y = HeightMapUtil.clamp(point.y, 0d, 1d);
        res.add(point);
      } else {
        return false;
      }
    }

    renPan.setControlPoints(controlPoints);
    renPan.setPoints(res);
    renPan.revalidate();
    renPan.repaint();
    return true;
  }
  
  private void deletedSelectedControlPoint() {
    int selIndex = renPan.getSelectedIndex();
    if(selIndex < 0) {
      return;
    }
        
    List<Vector2d> oldCps = new ArrayList<>(controlPoints);
    controlPoints.remove(renPan.getSelectedIndex());
    if(updateLine()) {
      renPan.setSelectedIndex(-1);
    } else {
      System.out.println("SplineRampEditor.deletedSelectedControlPoint: Failed delete");
      controlPoints = oldCps;
    }
    
  }

  private class InputHandler extends MouseAdapter implements KeyListener {

    private int selX;
    private int selY;
    private boolean isDragging;
    private boolean isMouseInPanel;

    @Override
    public void mousePressed(MouseEvent evt) {
      isMouseInPanel = true;
      if (evt.getButton() == 1) {
        int selectedIndex = renPan.getControlPointAt(evt.getX(), evt.getY());
        if (selectedIndex >= 0) {
          isDragging = true;
          selX = evt.getX();
          selY = evt.getY();
        }
        renPan.setSelectedIndex(selectedIndex);
        renPan.revalidate();
        renPan.repaint();

      } else {

        int[] offsets = renPan.getOffsets();
        int x = evt.getX() - offsets[0];
        int y = evt.getY() - offsets[1];
        double dx = (double) x / renPan.getRenderSize();
        double dy = (double) y / renPan.getRenderSize();
        dy = 1 - dy;
        Vector2d newPoint = new Vector2d(dx, dy);
        controlPoints.add(newPoint);
        if (!updateLine()) {
          controlPoints.remove(newPoint);
        }

      }

    }

    @Override
    public void mouseReleased(MouseEvent e) {
      isDragging = false;
    }

    @Override
    public void mouseExited(MouseEvent e) {
      isMouseInPanel = false;
    }

    @Override
    public void mouseEntered(MouseEvent e) {
      isMouseInPanel = true;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      if (!isDragging || !isMouseInPanel) {
        return;
      }

      double deltaX = (e.getX() - selX) / (double) renPan.getRenderSize();
      double deltaY = (e.getY() - selY) / (double) renPan.getRenderSize();

      Vector2d vec = controlPoints.get(renPan.getSelectedIndex());
      vec.x += deltaX;
      vec.y -= deltaY;

      vec.x = HeightMapUtil.clamp(vec.x, 0.001, 0.999);
      vec.y = HeightMapUtil.clamp(vec.y, 0.001, 0.999);

      if (updateLine()) {
        selX = e.getX();
        selY = e.getY();
      }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
      if(e.getKeyCode() == KeyEvent.VK_DELETE) {
        System.out.println("SplineRampEditor.InputHandler.keyPressed: delete");
        deletedSelectedControlPoint();
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

  }

}
