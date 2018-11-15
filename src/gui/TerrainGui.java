package gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import gui.ResourceFinder.ResourceEntry;
import render.TerrainApp;
import render.TerrainApp.WaterType;

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
  
  private JComboBox<ResourceFinder.ResourceEntry> hipsoCB;
  private JComboBox<ResourceFinder.ResourceEntry> bathCB;

  private JComboBox<WaterType> waterTypeCB;

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
    sunPan.add(new JLabel("Terrain Shading"));
    sunPan.add(hipsoCB);
    sunPan.add(new JLabel(" Water Shading"));
    sunPan.add(bathCB);
    
    JPanel waterPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    waterPan.setBorder(new TitledBorder("Water"));
    waterPan.add(new JLabel("Level"));
    waterPan.add(waterLevelSlider);
    waterPan.add(new JLabel("Type"));
    waterPan.add(waterTypeCB);

    // JPanel southPan = new JPanel(new GridLayout(3, 1));
    JPanel southPan = new JPanel();
    southPan.setLayout(new BoxLayout(southPan, BoxLayout.Y_AXIS));

    southPan.add(genParamsPan);
    southPan.add(sunPan);
    southPan.add(waterPan);

    JPanel mainPan = new JPanel(new BorderLayout());
    mainPan.add(canvas, BorderLayout.CENTER);
    mainPan.add(southPan, BorderLayout.SOUTH);

    updateSunPos();
    updateWaterLevel();

    frame.getContentPane().add(mainPan);
    frame.pack();
  }

  private void initComponenets() {
    samplePan = new IntPanel("Samps", 4, app.getTerainSize());
    octPan = new IntPanel("Oct", 3, app.getTerrainGen().getOctaves());
    roughPan = new DoublePanel("Rgh", 4, app.getTerrainGen().getRoughness());
    scalePan = new DoublePanel("Scale", 5, app.getTerrainGen().getScale());
    heightScalePan = new DoublePanel("Elv Scale", 3, app.getHeightScale());
    updateB = new JButton("Update");
    seedB = new JButton("Seed");

    sunSlider = new JSlider(0, 100, 30);
    sunSlider2 = new JSlider(0, 100, 30);
    waterLevelSlider = new JSlider(0, 100, (int)(app.getWaterLevel() * 100));
    
    Dimension sliderSize = new Dimension(100, waterLevelSlider.getPreferredSize().height);
    waterLevelSlider.setPreferredSize(sliderSize);
    sunSlider.setPreferredSize(sliderSize);
    sunSlider2.setPreferredSize(sliderSize);

    erodePan = new DoublePanel("Erode", 3, 0);

    waterTypeCB = new JComboBox<>(WaterType.values());
    waterTypeCB.setSelectedItem(app.getWaterType());

    frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    List<ResourceEntry> hipTex = ResourceFinder.INST.findTextures("hipso_");
    hipsoCB = new JComboBox<>(hipTex.toArray(new ResourceEntry[hipTex.size()]));
    ResourceEntry sel = find(hipTex, app.getHipsoTex());
    if(sel != null) {
      hipsoCB.setSelectedItem(sel);
    }
    
    List<ResourceEntry> bathTex = ResourceFinder.INST.findTextures("bath_");
    bathCB = new JComboBox<>(bathTex.toArray(new ResourceEntry[bathTex.size()]));
    sel = find(bathTex, app.getBathTexture());
    if(sel != null) {
      bathCB.setSelectedItem(sel);
    }
    
  }

  private ResourceEntry find(List<ResourceEntry> ents, String res) {
    for(ResourceEntry e : ents) {
      if(e.resource.equals(res)) {
        return e;
      }
    }
    return null;
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

    waterTypeCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.setWaterType(waterTypeCB.getItemAt(waterTypeCB.getSelectedIndex()));
          }
        });

      }
    });
    
    hipsoCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            ResourceEntry sel = hipsoCB.getItemAt(hipsoCB.getSelectedIndex());
            app.setHipsoTexture(sel.resource);
          }
        });

      }
    });
    
    bathCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            ResourceEntry sel = bathCB.getItemAt(bathCB.getSelectedIndex());
            app.setBathTexture(sel.resource);
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
    
    canvas.addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(ComponentEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.canvasResized();
          }
        });
      }

    });

  }

  public TerrainApp createApp() {
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

    return app;
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
    updateTerrain();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void updateSunPos() {
    int val = sunSlider.getValue();
    float ratio = val / 100f;
    double elevationAngle = Math.toRadians(ratio * 180);
    float x = (float) Math.cos(elevationAngle);
    float y = (float) Math.sin(elevationAngle);
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
    app.setHeightScale((float) heightScalePan.getVal());
    app.updateTerrain(samplePan.getVal(), octPan.getVal(), roughPan.getVal(), scalePan.getVal(), (float) erodePan.getVal());
  }

}
