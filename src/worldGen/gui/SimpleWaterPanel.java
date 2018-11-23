package worldGen.gui;

import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.JPanel;

import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import worldGen.gui.widget.ColorButton;
import worldGen.gui.widget.ColorButton.ColorChangeListener;
import worldGen.render.WorldRenderer;
import worldGen.util.TypeUtil;

public class SimpleWaterPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private ColorButton cb;
  
  public SimpleWaterPanel(final WorldRenderer ren) {
    
    Vector4f c4 = ren.getSimpleWaterColor();
    cb = new ColorButton(new Vector3f(c4.x, c4.y, c4.z));
    
    setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    add(cb);
    
    cb.addColorListener(new ColorChangeListener() {
      
      @Override
      public void colorChanged(Color newColor) {
        
        ren.enqueue(new Runnable() {
          @Override
          public void run() {
            ren.setSimpleWaterColor(TypeUtil.getColor4f(newColor));
          }
        });
        
      }
    });
  }
  
  
  
}
