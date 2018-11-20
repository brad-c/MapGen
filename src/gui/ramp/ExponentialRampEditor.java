package gui.ramp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector2d;

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

  public ExponentialRampEditor() {

    ramp = new ExponentialElevationRamp();

    renPan = new LineRenderPanel();
    renPan.setPreferredSize(new Dimension(800, 800));
    renPan.setFocusable(true);

    int selVal = (int)(getSliderRatioFromB(ramp.getB()) * 100);
    JSlider slider = new JSlider(1,100,selVal);
    slider.addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        float rat = slider.getValue()/100f;
        float b = getBFromSliderRatio(rat);
        ramp.setB(b);
        updateLine();
      }
    });
    //slider.setPreferredSize(new Dimension(500, slider.getPreferredSize().height));
    
    //JPanel southPan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//    southPan.add(slider);
    JPanel southPan = new JPanel(new BorderLayout());
    southPan.add(slider, BorderLayout.CENTER);
    
    rootPan = new JPanel(new BorderLayout());
    rootPan.add(renPan, BorderLayout.CENTER);
    rootPan.add(southPan, BorderLayout.SOUTH);
    
    

    updateLine();
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
