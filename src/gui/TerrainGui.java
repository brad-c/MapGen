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

import gen.SimpleNoiseGen;
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

  //private IntPanel samplePan;
  private JComboBox<Integer> sampleCB;
  private IntPanel octPan;
  private DoublePanel roughPan;
  private DoublePanel scalePan;
  private DoublePanel heightScalePan;
  private DoublePanel erodePan;
  private DoublePanel noiseMixPan;
  private LongPanel seedPan;

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

    JPanel genParamsPan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    
    genParamsPan1.add(new JLabel("Size"));
    genParamsPan1.add(sampleCB);
    genParamsPan1.add(octPan);
    genParamsPan1.add(roughPan);
    genParamsPan1.add(scalePan);
    genParamsPan1.add(heightScalePan);
    genParamsPan1.add(erodePan);
    
    JPanel genParamsPan2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    genParamsPan2.add(updateB);
    
    JPanel genParamsPan3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    genParamsPan3.add(seedPan);
    genParamsPan3.add(seedB);
    genParamsPan3.add(noiseMixPan);
    
    JPanel ugPan = new JPanel();
    ugPan.setLayout(new BoxLayout(ugPan, BoxLayout.X_AXIS));
    ugPan.add(genParamsPan3);
    ugPan.add(genParamsPan2);
    
    
    JPanel genParamsPan = new JPanel();
    genParamsPan.setBorder(new TitledBorder("Terrain Params"));
    genParamsPan.setLayout(new BoxLayout(genParamsPan, BoxLayout.Y_AXIS));
    genParamsPan.add(genParamsPan1);
    genParamsPan.add(ugPan);
        

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
    
    sampleCB = new JComboBox<>(new Integer[] {256,512,1024,2048});
    sampleCB.setSelectedItem(app.getTerainSize());
    
    octPan = new IntPanel("Oct", 2, app.getTerrainGen().getOctaves());
    roughPan = new DoublePanel("Rgh", 3, app.getTerrainGen().getRoughness());
    scalePan = new DoublePanel("Scale", 4, app.getTerrainGen().getScale());
    heightScalePan = new DoublePanel("Elv Scale", 3, app.getHeightScale());
    
    seedPan = new LongPanel("Seed", 15, app.getTerrainGen().getSeed());
    
    noiseMixPan = new DoublePanel("Noise Ratio", 3, app.getNoiseRatio());
    
    updateB = new JButton("Update");
    seedB = new JButton("Roll");

    sunSlider = new JSlider(0, 100, 30);
    sunSlider2 = new JSlider(0, 100, 30);
    waterLevelSlider = new JSlider(0, 100, (int)(app.getWaterLevel() * 100));
    
    Dimension sliderSize = new Dimension(100, waterLevelSlider.getPreferredSize().height);
    waterLevelSlider.setPreferredSize(sliderSize);
    sunSlider.setPreferredSize(sliderSize);
    sunSlider2.setPreferredSize(sliderSize);

    erodePan = new DoublePanel("Erode", 2, 0);

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
            app.getTerrainGen().setSeed(r.nextLong());
            seedPan.tf.setText(app.getTerrainGen().getSeed() + "");
            updateTerrain();
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
    
    app.setSize(sampleCB.getItemAt(sampleCB.getSelectedIndex()));
    app.setHeightScale((float) heightScalePan.getVal());
    app.setErodeFilter((float) erodePan.getVal());
    app.setNoiseRatio((float)noiseMixPan.getVal());
    
    SimpleNoiseGen gen = app.getTerrainGen();
    if(seedPan.getVal() != 0) {
      gen.setSeed(seedPan.getVal());
    }
    gen.setOctaves(octPan.getVal());
    gen.setRoughness(roughPan.getVal());
    gen.setScale(scalePan.getVal());
        
    app.updateTerrain();
  }

}
