package gui.ramp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import gen.ElevationRamp;
import gen.ExponentialElevationRamp;
import gen.SplineElevationRamp;

public class ElevationRampEditor {

  
  public static void showTestFrame() {
    ElevationRampEditor ed = new ElevationRampEditor();
    ed.addElevationRampListener(new ElevationRampListener() {
      @Override
      public void elevationRampChanged(ElevationRamp newRamp) {
        System.out.println("ElevationRampEditor{...}.elevationRampChanged: " + newRamp);
      }
    });
    JFrame f = new JFrame();
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(ed.getEditor());
    f.pack();
    f.setVisible(true);
  }
  
  private JPanel rootPan;

  private List<ElevationRampListener> listeners = new CopyOnWriteArrayList<>();

  private ExponentialRampEditor expEd;
  private SplineRampEditor splEd;

  private JTabbedPane tp;

  public ElevationRampEditor() {
    expEd = new ExponentialRampEditor();
    expEd.addElevationRampListener(new RampChangeList());
    splEd = new SplineRampEditor();
    splEd.addElevationRampListener(new RampChangeList());

    tp = new JTabbedPane();
    tp.addTab("Exponential", expEd.getEditor());
    tp.addTab("Spline", splEd.getEditor());
    tp.setSelectedIndex(0);

    tp.addChangeListener(new ChangeListener() {
      @Override
      public void stateChanged(ChangeEvent e) {
        int index = tp.getSelectedIndex();
        if(index == 0) {
          notifyListeners(expEd.getRamp());
        } else {
          notifyListeners(splEd.getRamp());
        }
      }
    });
    
    rootPan = new JPanel(new BorderLayout());
    rootPan.add(tp, BorderLayout.CENTER);
  }
  
  public void setElevationRamp(ElevationRamp ramp) {
    if(ramp instanceof ExponentialElevationRamp) {
      expEd.setRamp((ExponentialElevationRamp)ramp);
      tp.setSelectedIndex(0);
    } else if(ramp instanceof SplineElevationRamp) {
      splEd.setRamp((SplineElevationRamp)ramp);
      tp.setSelectedIndex(2);
    } else {
      System.out.println("ElevationRampEditor.getEditor: Unsupported ramp");
    }
  }

  public Component getEditor() {
    return rootPan;
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

  private void notifyListeners(ElevationRamp ramp) {
    for (ElevationRampListener l : listeners) {
      l.elevationRampChanged(ramp);
    }
  }

  private class RampChangeList implements ElevationRampListener {

    @Override
    public void elevationRampChanged(ElevationRamp newRamp) {
      notifyListeners(newRamp);
    }

  }

}
