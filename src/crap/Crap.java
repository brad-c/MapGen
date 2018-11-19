package crap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;

import gen.HeightMapUtil;

public class Crap {

  public static void main(String[] args) {
    new CurveTest();
  }

  private static class CurveTest {

    private GraphaPan pan;

    private List<Vector2d> controlPoints = new ArrayList<>();

    public CurveTest() {

      pan = new GraphaPan();
      pan.setPreferredSize(new Dimension(800, 800));

      MouseAdapter mousey = new MouseAdapter() {

        private int selectedIndex = -1;
        private int selX;
        private int selY;

        @Override
        public void mousePressed(MouseEvent evt) {
          if (evt.getButton() == 1) {
            selectedIndex = getControlPointAt(evt.getX(), evt.getY());
            if (selectedIndex >= 0) {
              System.out.println("Crap: Hit");
              selX = evt.getX();
              selY = evt.getY();
            } else {
              System.out.println("Crap: Miss");
            }
          } else {
            System.out.println("Crap.CurveTest.CurveTest().new MouseAdapter() {...}.mousePressed: ");
            int[] offsets = pan.getOffsets();
            int x = evt.getX() - offsets[0];
            int y = evt.getY() - offsets[1];
            double dx = (double)x / pan.getRenderSize();
            double dy = (double)x / pan.getRenderSize();
            
            Vector2d newPoint = new Vector2d(dx,dy);
            controlPoints.add(newPoint);
            if(!updateLine()) {
              controlPoints.remove(newPoint);
            }
            
          }
          
        }

        @Override
        public void mouseReleased(MouseEvent e) {
          selectedIndex = -1;
        }

        @Override
        public void mouseExited(MouseEvent e) {
          selectedIndex = -1;
        }

        @Override
        public void mouseDragged(MouseEvent e) {
          if (selectedIndex < 0) {
            return;
          }

          double deltaX = (e.getX() - selX) / (double) pan.getRenderSize();
          double deltaY = (e.getY() - selY) / (double) pan.getRenderSize();

          Vector2d vec = controlPoints.get(selectedIndex);
          vec.x += deltaX;
          vec.y -= deltaY;

          vec.x = HeightMapUtil.clamp(vec.x, 0.11, 0.99);
          vec.y = HeightMapUtil.clamp(vec.y, 0.11, 0.99);

          if (updateLine()) {
            selX = e.getX();
            selY = e.getY();
          }
        }

      };

      pan.addMouseListener(mousey);
      pan.addMouseMotionListener(mousey);

      controlPoints.add(new Vector2d(0.5, 0.5));
      updateLine();

      JButton updateB = new JButton("Update");
      updateB.addActionListener(new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
          updateLine();
        }
      });

      JPanel bp = new JPanel(new FlowLayout(FlowLayout.RIGHT));
      bp.add(updateB);

      JPanel mp = new JPanel(new BorderLayout());
      mp.add(pan, BorderLayout.CENTER);
      mp.add(bp, BorderLayout.SOUTH);

      JFrame f = new JFrame();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.getContentPane().add(mp);
      f.pack();
      f.setVisible(true);
    }

    private int getControlPointAt(int x, int y) {
      
      int[] offsets = pan.getOffsets();
      x = x - offsets[0];
      y = y - offsets[1];
      
      int panSize = pan.getRenderSize();
      // flip y
      y = panSize - y;
      
      // do all calcs in normalised space
      Vector2d click = new Vector2d((double) x / panSize, (double) y / panSize);
      double halfRange = pan.cpSize / (double) panSize / 2;
      int index = 0;
      for (Vector2d cp : controlPoints) {
        System.out.println("Crap.CurveTest.getControlPointAt: " + cp);
        Vector2d min = new Vector2d(cp.x - halfRange, cp.y - halfRange);
        Vector2d max = new Vector2d(cp.x + halfRange, cp.y + halfRange);
        if (inBounds(click, min, max)) {
          return index;
        }
        index++;
      }
      return -1;
    }

    private boolean inBounds(Vector2d cp, Vector2d min, Vector2d max) {
      return cp.x >= min.x && cp.x <= max.x && cp.y >= min.y && cp.y <= max.y;
    }

    private boolean updateLine() {
      
      Collections.sort(controlPoints, new Comparator<Vector2d>() {

        @Override
        public int compare(Vector2d o1, Vector2d o2) {
          return Double.compare(o1.x, o2.x);
        }
      });
      
      List<Vector2d> cps = new ArrayList<>(controlPoints.size() + 2);
      cps.add(new Vector2d(0.0, 0.0));
      // deep copy
      for (Vector2d cp : controlPoints) {
        cps.add(new Vector2d(cp));
      }
      cps.add(new Vector2d(1.0, 1.0));

      double[] cpX = new double[cps.size()];
      double[] cpY = new double[cps.size()];
      for (int i = 0; i < cps.size(); i++) {
        Vector2d cp = cps.get(i);
        cpX[i] = cp.x;
        cpY[i] = cp.y;
      }
      int numSamples = 1000;
      Vector2d[] res = new Vector2d[numSamples];

      SplineInterpolator si = new SplineInterpolator();
      PolynomialSplineFunction interpa = null;
      try {
        interpa = si.interpolate(cpX, cpY);
      } catch (Exception e) {
        System.out.println("Crap.CurveTest.calcLine: " + e);
        return false;
      }
      System.out.println("Crap.CurveTest.updateLine: ");

      for (int i = 0; i < numSamples; i++) {
        double rat = (double) i / (numSamples - 1);
        if (interpa.isValidPoint(rat)) {
          res[i] = new Vector2d(rat, interpa.value(rat));
          res[i].y = HeightMapUtil.clamp(res[i].y, 0d, 1d);
        } else {
          //res[i] = new Vector2d(rat, 0);
          System.out.println("Crap.CurveTest.updateLine: !!!!!");
          return false;
        }
      }

      pan.controlPoints = cps;
      pan.points = res;
      pan.revalidate();
      pan.repaint();
      return true;
    }

  }

  private static class GraphaPan extends JPanel {

    private Vector2d[] points;

    private List<Vector2d> controlPoints;

    private int cpSize = 12;
    
    public int[] getOffsets() {
      int w = getWidth();
      int h = getHeight();
      int xOffset = 0;
      int yOffset = 0;
      if (w > h) {
        xOffset = (w - h) / 2;
      } else if (h > w) {
        yOffset = (h - w) / 2;
      }
      return new int[] {xOffset,yOffset};
    }

    @Override
    public void paint(Graphics g) {
      int w = getWidth();
      int h = getHeight();
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, w, h);

      h = Math.min(w, h);
      w = h;
      
      int[] o = getOffsets();
      g.translate(o[0],o[1]);

      g.setColor(Color.WHITE);
      g.fillRect(0, 0, w, h);

      if (controlPoints != null) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < controlPoints.size(); i++) {
          Vector2d loc = controlPoints.get(i);
          int x = (int) (loc.x * w) - cpSize / 2;
          int y = (int) (loc.y * h) + cpSize / 2;
          y = h - y;
          g.setColor(Color.BLACK);
          g.fillRect(x, y, cpSize, cpSize);
          g.setColor(Color.CYAN);
          int indent = 3;
          g.fillRect(x + indent, y + indent, cpSize - indent * 2, cpSize - indent * 2);
        }
      }

      if (points != null) {
        g.setColor(Color.BLACK);
        for (int i = 0; i < points.length - 1; i++) {
          Vector2d from = points[i];
          Vector2d to = points[i + 1];

          // flip y
          from = new Vector2d(from);
          from.y = 1 - from.y;
          to = new Vector2d(to);
          to.y = 1 - to.y;

          g.drawLine((int) (from.x * w), (int) (from.y * h), (int) (to.x * w), (int) (to.y * h));
        }
      }

    }

    public int getRenderSize() {
      return getWidth() > getHeight() ? getHeight() : getWidth();
    }

  }

}
