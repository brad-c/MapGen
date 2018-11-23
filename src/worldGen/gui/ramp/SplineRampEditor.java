package worldGen.gui.ramp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import worldGen.gen.ElevationRamp;
import worldGen.gen.HeightMapUtil;
import worldGen.gen.SplineElevationRamp;

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
  private LineRenderPanel renPan;

  private List<Vector2d> controlPoints;
  private JButton removeB;

  private SplineElevationRamp ramp = new SplineElevationRamp();

  private List<ElevationRampListener> listeners = new CopyOnWriteArrayList<>();

  public SplineRampEditor() {
    this(true);
  }

  public SplineRampEditor(boolean north) {

    controlPoints = new ArrayList<>();

    renPan = new LineRenderPanel();
    renPan.setPreferredSize(new Dimension(400, 400));
    renPan.setFocusable(true);

    InputHandler ih = new InputHandler();
    renPan.addMouseListener(ih);
    renPan.addMouseMotionListener(ih);
    renPan.addKeyListener(ih);

    JButton addB = new JButton("Add");
    removeB = new JButton("Remove");
    removeB.setEnabled(false);
    removeB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        deletedSelectedControlPoint();
      }
    });
    addB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        addControlPoint();
      }

    });

    JPanel southPan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    southPan.add(addB);
    southPan.add(removeB);

    rootPan = new JPanel(new BorderLayout());
    rootPan.add(renPan, BorderLayout.CENTER);
    if(north) {
      rootPan.add(southPan, BorderLayout.NORTH);
    } else {
      rootPan.add(southPan, BorderLayout.SOUTH);
    }

    controlPoints.add(new Vector2d(0.5, 0.5));
    updateLine();
  }

  public LineRenderPanel getGraphRenderer() {
    return renPan;
  }
  
  public Component getEditor() {
    return rootPan;
  }

  public ElevationRamp getRamp() {
    return ramp;
  }

  public void setRamp(SplineElevationRamp ramp) {
    this.ramp = ramp;
    controlPoints = ramp.getControlPoints();
    updateLine();
  }

  private boolean updateRamp() {
    SplineElevationRamp ramp = new SplineElevationRamp();
    if (ramp.setControlPoints(controlPoints)) {
      this.ramp = ramp;
      for (ElevationRampListener l : listeners) {
        l.elevationRampChanged(ramp);
      }
      return true;
    } else {
      System.out.println("SplineRampEditor: Invalid new ramp ");
      return false;
    }
  }

  public void addElevationRampListener(ElevationRampListener listener) {
    if (listener != null) {
      listeners.add(listener);
    }
  }

  public void removeElevationRampListener(ElevationRampListener listener) {
    if (listener != null) {
      listeners.remove(listener);
    }
  }

  private boolean updateLine() {

    Collections.sort(controlPoints, new Comparator<Vector2d>() {

      @Override
      public int compare(Vector2d o1, Vector2d o2) {
        return Double.compare(o1.x, o2.x);
      }
    });

    if (!updateRamp()) {
      return false;
    }
    if (ramp == null) {
      return false;
    }

    int numSamples = 1000;
    List<Vector2d> res = new ArrayList<>(numSamples);

    for (int i = 0; i < numSamples; i++) {
      double rat = (double) i / (numSamples - 1);
      if (ramp.isValidPoint(rat)) {
        Vector2d point = new Vector2d(rat, ramp.applyRamp((float) rat));
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
    if (selIndex < 0) {
      return;
    }
    List<Vector2d> oldCps = new ArrayList<>(controlPoints);
    controlPoints.remove(renPan.getSelectedIndex());
    if (updateLine()) {
      setSelectedIndex(-1);
    } else {
      System.out.println("SplineRampEditor.deletedSelectedControlPoint: Failed delete");
      controlPoints = oldCps;
    }
  }

  private void addControlPoint() {

    double xVal = 0.5;
    int tries = 0;
    boolean changed = true;
    while (changed && tries < 100) {
      double newVal = checkX(xVal);
      if (newVal != xVal) {
        changed = true;
        xVal = newVal;
      } else {
        changed = false;
      }
      tries++;
    }

    if (!ramp.isValidPoint(xVal)) {
      System.out.println("SplineRampEditor.addControlPoint: Invlaid x value: " + xVal);
      return;
    }
    double yVal = ramp.applyRamp((float) xVal);
    Vector2d newPoint = new Vector2d(xVal, yVal);
    controlPoints.add(newPoint);
    if (!updateLine()) {
      controlPoints.remove(newPoint);
    }

  }

  private double checkX(double xVal) {
    for (Vector2d cp : controlPoints) {
      if (Math.abs(cp.x - xVal) < 0.01) {
        xVal += 0.01;
      }
    }
    return xVal;
  }

  private void setSelectedIndex(int i) {
    renPan.setSelectedIndex(i);
    removeB.setEnabled(i >= 0 && controlPoints.size() > 1);
  }

  private class InputHandler extends MouseAdapter implements KeyListener {

    private int selX;
    private int selY;
    private boolean isDragging;
    private boolean isMouseInPanel;

    @Override
    public void mousePressed(MouseEvent evt) {
      isMouseInPanel = true;
      renPan.requestFocus();
      if (evt.getButton() == 1) {
        int selectedIndex = renPan.getControlPointAt(evt.getX(), evt.getY());
        if (selectedIndex >= 0) {
          isDragging = true;
          selX = evt.getX();
          selY = evt.getY();
        }
        setSelectedIndex(selectedIndex);
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
      if (e.getKeyCode() == KeyEvent.VK_DELETE) {
        deletedSelectedControlPoint();
      }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

  }

}
