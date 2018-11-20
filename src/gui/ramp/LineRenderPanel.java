package gui.ramp;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;
import javax.vecmath.Vector2d;

public class LineRenderPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private List<Vector2d> points;

  private List<Vector2d> controlPoints;

  private int selectedIndex = -1;

  private int cpSize = 12;
  
  public LineRenderPanel() {
  }

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
    return new int[] { xOffset, yOffset };
  }

  @Override
  public void paint(Graphics g) {
    
    Graphics2D g2 = (Graphics2D)g;
    RenderingHints rh = new RenderingHints(
             RenderingHints.KEY_ANTIALIASING,
             RenderingHints.VALUE_ANTIALIAS_ON);
    g2.setRenderingHints(rh);
    
    int w = getWidth();
    int h = getHeight();
    g.setColor(Color.BLACK);
    g.fillRect(0, 0, w, h);

    h = Math.min(w, h);
    w = h;

    int[] o = getOffsets();
    g.translate(o[0], o[1]);

    g.setColor(Color.WHITE);
    g.fillRect(0, 0, w, h);

    if (controlPoints != null) {
      g.setColor(Color.BLACK);
      for (int i = 0; i < controlPoints.size(); i++) {

        Vector2d loc = controlPoints.get(i);
        int x = (int) (loc.x * w) - cpSize / 2;
        int y = (int) (loc.y * h) + cpSize / 2;
        y = h - y;

        if (i == selectedIndex) {
          int thick = 3;
          g.setColor(Color.RED);
          g.fillRect(x - thick, y - thick, cpSize + thick * 2, cpSize + thick * 2);
        }

        g.setColor(Color.BLACK);
        g.fillRect(x, y, cpSize, cpSize);
        g.setColor(Color.WHITE);
        int indent = 3;
        g.fillRect(x + indent, y + indent, cpSize - indent * 2, cpSize - indent * 2);

      }
    }

    if (points != null) {
      g.setColor(Color.BLACK);
      for (int i = 0; i < points.size() - 1; i++) {
        Vector2d from = points.get(i);
        Vector2d to = points.get(i + 1);

        // flip y
        from = new Vector2d(from);
        from.y = 1 - from.y;
        to = new Vector2d(to);
        to.y = 1 - to.y;

        g.drawLine((int) (from.x * w), (int) (from.y * h), (int) (to.x * w), (int) (to.y * h));
      }
    }

  }

  public int getControlPointAt(int x, int y) {

    if(controlPoints == null) {
      return - 1;
    }
    
    int[] offsets = getOffsets();
    x = x - offsets[0];
    y = y - offsets[1];

    int panSize = getRenderSize();
    // flip y
    y = panSize - y;

    // do all calcs in normalised space
    Vector2d click = new Vector2d((double) x / panSize, (double) y / panSize);
    double halfRange = cpSize / (double) panSize / 2;
    int index = 0;
    for (Vector2d cp : controlPoints) {
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
  
  public int getRenderSize() {
    return getWidth() > getHeight() ? getHeight() : getWidth();
  }

  public void setPoints(List<Vector2d> newPoints) {
    points = new ArrayList<>(newPoints == null ? 0 : newPoints.size());
    if(newPoints == null) {
      return;
    }
    for(Vector2d p : newPoints) {
      points.add(new Vector2d(p));
    }
  }

  public void setControlPoints(List<Vector2d> newPoints) {
    controlPoints = new ArrayList<>(newPoints == null ? 0 : newPoints.size());
    if(newPoints == null) {
      return;
    }
    for(Vector2d p : newPoints) {
      controlPoints.add(new Vector2d(p));
    }
    
  }

  public void setSelectedIndex(int selectedIndex) {
    this.selectedIndex = selectedIndex;
  }
  
  public int getSelectedIndex() {
    return selectedIndex;
  }
  
  public int getControlPointSize() {
    return cpSize;
  }

}
