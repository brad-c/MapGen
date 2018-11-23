package worldGen.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import worldGen.gui.widget.ColorButton;
import worldGen.gui.widget.ColorButton.ColorChangeListener;
import worldGen.render.PurdyWater;
import worldGen.render.WorldRenderer;
import worldGen.util.TypeUtil;

public class PurdyWaterPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private ColorButton colorB;
  private ColorButton deepColorB;
  private JSlider transpSlider;

  public PurdyWaterPanel(final WorldRenderer ren) {

    final PurdyWater water = ren.getPurdyWater();

    colorB = new ColorButton(TypeUtil.getColor3f(water.getWaterColor()));
    deepColorB = new ColorButton(TypeUtil.getColor3f(water.getDeepWaterColor()));

    transpSlider = new JSlider(0,100,(int)(water.getWaterTransparency() * 100));
    transpSlider.setPreferredSize(new Dimension(80, transpSlider.getPreferredSize().height));
    
    
    setLayout(new FlowLayout(FlowLayout.LEFT));
    
    add(new JLabel("Depth Fog:"));
    add(deepColorB);
    add(new JLabel("Surface:"));
    add(colorB);
    add(new JLabel("Transp:"));
    add(transpSlider);

    colorB.addColorListener(new ColorChangeListener() {
      @Override
      public void colorChanged(Color newColor) {
        ren.enqueue(new Runnable() {
          @Override
          public void run() {
            water.setWaterColor(TypeUtil.getColorRGBA(newColor));
          }
        });
      }
    });

    deepColorB.addColorListener(new ColorChangeListener() {
      @Override
      public void colorChanged(Color newColor) {
        ren.enqueue(new Runnable() {
          @Override
          public void run() {
            water.setDeepWaterColor(TypeUtil.getColorRGBA(newColor));
          }
        });
      }
    });
    
    transpSlider.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        ren.enqueue(new Runnable() {
          @Override
          public void run() {
            float val = transpSlider.getValue() / 100f;
            water.setWaterTransparency(val);
          }
        });

      }
    });
  }

}
