package worldGen.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
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
    
    JPanel pan = new JPanel(new GridBagLayout());
    pan.setBorder(new TitledBorder("Terrain"));
    int y = 0;
    Insets insets = new Insets(0, 0, 0, 0);
    y++;
    pan.add(new JLabel("Surface:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    pan.add(colorB, new GridBagConstraints(1, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    pan.add(new JLabel("Transp:"), new GridBagConstraints(2, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    pan.add(transpSlider, new GridBagConstraints(3, y, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    y++;
    pan.add(new JLabel("Depth Fog:"), new GridBagConstraints(0, y, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    pan.add(deepColorB, new GridBagConstraints(1, y, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    
    
    setLayout(new BorderLayout());
    add(pan, BorderLayout.CENTER);

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
