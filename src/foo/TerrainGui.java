package foo;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

public class TerrainGui {

  public static void main(String[] args) {

    TerrainGui terrainGui = new TerrainGui();
    terrainGui.createApp();
    try {
      Thread.sleep(500);
    } catch (InterruptedException ex) {
    }

    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        terrainGui.createGui();
        terrainGui.startApp();
        terrainGui.show();
      }

    });
  }

  private TerrainApp app;
  private Canvas canvas;
  private JFrame frame;

  private IntPanel samplePan;
  private IntPanel octPan;
  private DoublePanel roughPan;
  private DoublePanel scalePan;
  private DoublePanel heightScalePan;
  private DoublePanel erodePan;
  
  private JSlider sunSlider;
  private JSlider sunSlider2;
  
  private JSlider waterLevelSlider;
  private JButton updateB;
  private JButton seedB;

  public TerrainGui() {

  }

  private void createGui() {

    initComponenets();

    addListeners();

    JPanel genParamsPan = new JPanel(new FlowLayout());
    genParamsPan.setBorder(new TitledBorder("Terrain Params"));
    genParamsPan.add(samplePan);
    genParamsPan.add(octPan);
    genParamsPan.add(roughPan);
    genParamsPan.add(scalePan);
    genParamsPan.add(heightScalePan);
    genParamsPan.add(erodePan);
    genParamsPan.add(seedB);
    genParamsPan.add(updateB);
    
    
    JPanel sunPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    sunPan.setBorder(new TitledBorder("Visuals"));
    sunPan.add(new JLabel("Sun"));
    sunPan.add(sunSlider);
    sunPan.add(sunSlider2);
    sunPan.add(new JLabel("Water"));
    sunPan.add(waterLevelSlider);
    
    
    
    JPanel southPan = new JPanel(new GridLayout(2, 1));
    southPan.add(genParamsPan);
    southPan.add(sunPan);
    
    JPanel mainPan = new JPanel(new BorderLayout());
    mainPan.add(canvas, BorderLayout.CENTER);
    mainPan.add(southPan, BorderLayout.SOUTH);

    
    updateSunPos();
    updateWaterLevel();
    updateTerrain();
    
    
    frame.getContentPane().add(mainPan);
    frame.pack();
  }

  private void initComponenets() {
    samplePan = new IntPanel("Samps", 4, 256);
    octPan = new IntPanel("Oct", 3, 7);
    roughPan = new DoublePanel("Rgh", 4, 0.6);
    scalePan = new DoublePanel("Scale", 5, 0.005);
    heightScalePan = new DoublePanel("Elv Scale", 3, app.getHeightScale());
    updateB = new JButton("Update");
    seedB = new JButton("Seed");
    
    sunSlider = new JSlider(0,100,30);
    sunSlider2 = new JSlider(0,100,30);
    waterLevelSlider = new JSlider(0,100,50);
    
    erodePan = new DoublePanel("Erode", 3, 0);
    
    frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

  private void addListeners() {
    updateB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            updateTerrain();
          }
        });
      }
    });

    seedB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            Random r = new Random();
            app.updateTerrain(r.nextLong());
          }
        });

      }
    });
    
    ChangeListener sunListener = (new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            updateSunPos();
          }
        });
        
      }
    });
    
    sunSlider.addChangeListener(sunListener);
    sunSlider2.addChangeListener(sunListener);
    
    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        app.stop();
      }
    });
    
    waterLevelSlider.addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            updateWaterLevel();
          }
          
        });
        
      }
    });
    
    
  }

  public void createApp() {
    AppSettings settings = new AppSettings(true);
    settings.setWidth(640);
    settings.setHeight(480);

    app = new TerrainApp();

    app.setPauseOnLostFocus(false);
    app.setSettings(settings);
    app.createCanvas();
    app.startCanvas();

    JmeCanvasContext context = (JmeCanvasContext) app.getContext();
    canvas = context.getCanvas();
    canvas.setSize(settings.getWidth(), settings.getHeight());
  }

  private void startApp() {
    app.startCanvas();
    app.enqueue(new Callable<Void>() {
      @Override
      public Void call() {
        app.getFlyByCamera().setDragToRotate(true);
        return null;
      }
    });
  }

  private void show() {
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
  
  private void updateSunPos() {
    int val = sunSlider.getValue();
    float ratio = val / 100f;
    double elevationAngle = Math.toRadians(ratio * 180);
    float x = (float)Math.cos(elevationAngle);
    float y = (float)Math.sin(elevationAngle);
    float z = 0;
    
    
    val = sunSlider2.getValue();
    ratio = val / 100f;
    double rotAngle = Math.toRadians(ratio * 360);
    
    Matrix4d m = new Matrix4d();
    m.setIdentity();
    m.rotY(rotAngle);

    Vector3f vec = new Vector3f(x, y, z);
    m.transform(vec);
    vec.normalize();
    
    app.setSunDirection(new com.jme3.math.Vector3f(vec.x, vec.y, vec.z));
  }
  
  private void updateWaterLevel() {
    int val = waterLevelSlider.getValue();
    float ratio = val / 100f;
    app.setWaterLevel(ratio);
  }
  
  private void updateTerrain() {
    app.setHeightScale((float)heightScalePan.getVal());
    app.updateTerrain(samplePan.getVal(), octPan.getVal(), roughPan.getVal(), scalePan.getVal(), (float)erodePan.getVal());
  }

  private class NumPanel extends JPanel {

    JTextField tf;

    public NumPanel(String label, int size) {
      setLayout(new FlowLayout());
      add(new JLabel(label + ":"));
      tf = new JTextField(size);
      add(tf);
    }

  }

  private class IntPanel extends NumPanel {

    private int defVal;

    public IntPanel(String label, int size, int defVal) {
      super(label, size);
      this.defVal = defVal;
      tf.setText(defVal + "");
    }

    public int getVal() {
      try {
        return Integer.parseInt(tf.getText());
      } catch (Exception e) {
        tf.setText(defVal + "");
        return defVal;
      }
    }

  }

  private class DoublePanel extends NumPanel {

    private double defVal;
    // private NumberFormat formatter = new DecimalFormat("#0.00000");
    private NumberFormat formatter = new DecimalFormat();

    public DoublePanel(String label, int size, double defVal) {
      super(label, size);
      this.defVal = defVal;
      tf.setText(formatter.format(defVal));
    }

    public double getVal() {
      try {
        return Double.parseDouble(tf.getText());
      } catch (Exception e) {
        tf.setText(formatter.format(defVal));
        return defVal;
      }
    }

  }

}
