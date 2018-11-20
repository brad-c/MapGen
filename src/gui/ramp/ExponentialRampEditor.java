package gui.ramp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector2d;

import gen.ElevationRamp;
import gen.ExponentialElevationRamp;
import gen.HeightMapUtil;

public class ExponentialRampEditor {

  public static void showTestFrame() {
    ExponentialRampEditor ed = new ExponentialRampEditor();
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(ed.getEditor());
    f.pack();
    f.setVisible(true);
  }

  private JPanel rootPan;
  private LineRenderPanel renPan;

  private ExponentialElevationRamp ramp;
  
  private List<ElevationRampListener> listeners = new CopyOnWriteArrayList<>();

  public ExponentialRampEditor() {

    ramp = new ExponentialElevationRamp();

    renPan = new LineRenderPanel();
    renPan.setPreferredSize(new Dimension(400, 400));
    renPan.setFocusable(true);

    int selVal = (int)(getSliderRatioFromB(ramp.getB()) * 100);
    JSlider slider = new JSlider(1,100,selVal);
    //slider.setPreferredSize(new Dimension(600, slider.getPreferredSize().height));
    slider.addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        float rat = slider.getValue()/100f;
        float b = getBFromSliderRatio(rat);
        ramp.setB(b);
        
        for(ElevationRampListener l : listeners) {
          l.elevationRampChanged(ramp);
        }
        
        updateLine();
      }
    });
    

    JPanel sliderPan = new JPanel(new BorderLayout());
    sliderPan.setBorder(new EmptyBorder(6, 6, 6, 6));
    sliderPan.add(new JLabel("Slope: "), BorderLayout.WEST);
    sliderPan.add(slider, BorderLayout.CENTER);
    
    JPanel southPan = new JPanel(new BorderLayout());
    southPan.add(sliderPan, BorderLayout.CENTER);
    
    rootPan = new JPanel(new BorderLayout());
    rootPan.add(renPan, BorderLayout.CENTER);
    rootPan.add(southPan, BorderLayout.SOUTH);

    updateLine();
  }

  public ElevationRamp getRamp() {
    return ramp;
  }
  
  public void setRamp(ExponentialElevationRamp ramp) {
    this.ramp = ramp;
    updateLine();
  }
  
  public void addElevationRampListener(ElevationRampListener listener) {
    if(listener != null) {
      listeners.add(listener);
    }
  }
  
  public void removeElevationRampListener(ElevationRampListener listener) {
    if(listener != null) {
      listeners.remove(listener);
    }
  }
  
  private float getBFromSliderRatio(float in) {
    return (float)Math.pow(10000, in);
  }
  
  private float getSliderRatioFromB(float in) {
    double val = Math.log(in)/Math.log(10000);
    return (float)val;
  }
  
  public Component getEditor() {
    return rootPan;
  }

  private boolean updateLine() {

    int numSamples = 1000;
    List<Vector2d> res = new ArrayList<>(numSamples);

    for (int i = 0; i < numSamples; i++) {
      double rat = (double) i / (numSamples - 1);
      Vector2d point = new Vector2d(rat, ramp.applyRamp((float)rat));
      point.y = HeightMapUtil.clamp(point.y, 0d, 1d);
      res.add(point);
    }
    renPan.setPoints(res);
    renPan.revalidate();
    renPan.repaint();
    return true;
  }

  

}
