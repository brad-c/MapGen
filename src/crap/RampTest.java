package crap;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.vecmath.Vector2d;

import gen.DefaultElevationRamp;
import gen.HeightMapUtil;

public class RampTest {

  public static void main(String[] args) {
    new RampTest();
  }

  private GraphaPan pan;

  public RampTest() {
    pan = new GraphaPan();
    pan.setPreferredSize(new Dimension(800, 800));

    JButton updateB = new JButton("Update");
    updateB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        calcLine();
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

  private void calcLine() {

    int numPoints = 50;

    Vector2d[] res = new Vector2d[numPoints];
    
    // straight line
    float[] heights = new float[numPoints];
    for (int i = 0; i < numPoints; i++) {
      float x = (float) (i + 1) / numPoints;
      heights[i] = x;
    }

    DefaultElevationRamp ramp = new DefaultElevationRamp();
    heights = ramp.apply(heights, 0.5f, 1);

    for (int i = 0; i < numPoints; i++) {
      float x = (float) (i + 1) / numPoints;
      res[i] = new Vector2d(x, heights[i]);
    }

    pan.points = res;
    pan.revalidate();
    pan.repaint();
  }

  private double getY(double x) {

    // func = y = b^x - 1
    double b = 100;

    double val = Math.pow(b, x) - 1;

    // normalise
    val = HeightMapUtil.normalise(val, 0, (Math.pow(b, 1) - 1));

    return val;
  }

  private static class GraphaPan extends JPanel {

    private Vector2d[] points;

    @Override
    public void paint(Graphics g) {
      int w = getWidth();
      int h = getHeight();
      g.setColor(Color.WHITE);
      g.fillRect(0, 0, w, h);
      g.setColor(Color.GREEN);
      g.drawRect(0, 0, w - 1, h - 1);

      if (points == null) {
        return;
      }

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

}
