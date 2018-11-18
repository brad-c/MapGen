package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JColorChooser;

import com.jme3.math.Vector3f;

public class ColorButton extends JButton {
  
  private static final long serialVersionUID = 1L;
  
  private Color color;
  private int size = 16;

  private List<ColorChangeListener> listeners = new CopyOnWriteArrayList<>();
  
  public ColorButton() {
    this(Color.WHITE);
  }
  
  public ColorButton(Vector3f colf) {
    this(new Color(colf.x, colf.y, colf.z));
  }
  
  public ColorButton(Color color) {
    this.color = color;
    setIcon(new ColorIcon());
    int m = 2;
    setMargin(new Insets(m, m, m, m));
    
    addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        onPress();
      }
    });
  }

  public Color getColor() {
    return color;
  }
  
  public Vector3f getColor3f() {
    return new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
  }

  public void setColor(Color color) {
    this.color = color;
  }
  
  public void addColorListener(ColorChangeListener l) {
    if(l != null) {
      listeners.add(l);
    }
  }
  
  public void removeColorListener(ColorChangeListener l) {
    if(l != null) {
      listeners.add(l);
    }
  }

  private void onPress() {
    Color val = JColorChooser.showDialog(this, "Select Color", color);
    if (val != null) {
      setColor(val);
      for(ColorChangeListener l : listeners) {
        l.colorChanged(val);
      }
    }
  }
  
  public static interface ColorChangeListener {
    public void colorChanged(Color newColor);
  }

  private class ColorIcon implements Icon {

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
      g.setColor(color);
      g.fillRect(x, y, getIconWidth(), getIconHeight());
    }

    @Override
    public int getIconWidth() {
      return size;
    }

    @Override
    public int getIconHeight() {
      return size;
    }

  }

  

}
