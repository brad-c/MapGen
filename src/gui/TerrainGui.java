package gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
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
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import gen.SimplexNoiseGen;
import gui.ResourceFinder.ResourceEntry;
import gui.widget.ColorButton;
import gui.widget.ColorButton.ColorChangeListener;
import gui.widget.DoublePanel;
import gui.widget.IntPanel;
import gui.widget.LongPanel;
import render.ColorFilter;
import render.TerrainGenerator;
import render.WorldRenderer;
import render.WorldRenderer.ViewType;
import render.WorldRenderer.WaterType;
import util.TypeUtil;

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

  private WorldRenderer app;
  private Canvas canvas;
  private JFrame frame;

  //View
  private JToggleButton view3dB;
  private JToggleButton view2dB;
  
  //Noise
  private IntPanel octPan;
  private DoublePanel roughPan;
  private DoublePanel scalePan;
  private DoublePanel erodePan;
  private LongPanel seedPan;
  
  //Terrain
  private JComboBox<Integer> resolutionCB;
  private DoublePanel noiseMixPan;
  private DoublePanel heightScalePan;
  private JButton updateB;
  private JButton seedB;

  //Shading
  private JSlider sunSlider;
  private JSlider sunSlider2;
  private JSlider ambientSlider;
  private JComboBox<ResourceFinder.ResourceEntry> hipsoCB;
  private JComboBox<ResourceFinder.ResourceEntry> bathCB;
  
  //Coastline
  private JCheckBox coastlineCB;
  private JSlider coastlineThicknessSlider;
  private ColorButton coastlineColorB;
  
  //Water
  private JComboBox<WaterType> waterTypeCB;
  private JSlider waterLevelSlider;
    
  //Color Filter
  private JCheckBox colorFilterEnabledCB;
  private JCheckBox blackAndWhiteEnabledCB;
  private JCheckBox invertColorsEnabledCB;
  private JSlider brightnessSlider;
  private JSlider contrastSlider;
  
  //layout
  private JPanel rootPan;
  private JPanel waterDetailsPan;

  public TerrainGui() {
  }

  private void createGui() {

    initComponenets();
    addComponents();
    addListeners();

    //We are pushing these values atm
    updateSunPos();
    updateWaterLevel();

    frame.getContentPane().add(rootPan);
    frame.pack();
  }

  private void addComponents() {
    JPanel genParamsPan1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    genParamsPan1.setBorder(new TitledBorder("Noise"));
    genParamsPan1.add(seedPan);
    genParamsPan1.add(seedB);
    genParamsPan1.add(octPan);
    genParamsPan1.add(roughPan);
    genParamsPan1.add(scalePan);
    
    JPanel genParamsPan2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    genParamsPan2.setBorder(new TitledBorder("Terrain"));
    genParamsPan2.add(new JLabel("Resolution"));
    genParamsPan2.add(resolutionCB);
    genParamsPan2.add(heightScalePan);
    genParamsPan2.add(erodePan);
    genParamsPan2.add(noiseMixPan);
    genParamsPan2.add(updateB);
    
    JPanel genParamsPan = new JPanel();
    genParamsPan.setBorder(new TitledBorder("Terrain Definition"));
    genParamsPan.setLayout(new BoxLayout(genParamsPan, BoxLayout.Y_AXIS));
    genParamsPan.add(genParamsPan1);
    genParamsPan.add(genParamsPan2);
        
    JPanel sunPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    sunPan.setBorder(new TitledBorder("Shading"));
    sunPan.add(new JLabel("Sun"));
    sunPan.add(sunSlider);
    sunPan.add(sunSlider2);
    sunPan.add(new JLabel("Ambient"));
    sunPan.add(ambientSlider);
    sunPan.add(new JLabel("Terrain Shading"));
    sunPan.add(hipsoCB);
    sunPan.add(new JLabel(" Water Shading"));
    sunPan.add(bathCB);
    
    waterDetailsPan = new JPanel(new BorderLayout(0,0));
    JPanel waterPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    waterPan.setBorder(new TitledBorder("Water"));
    waterPan.add(new JLabel("Level"));
    waterPan.add(waterLevelSlider);
    waterPan.add(new JLabel("Type"));
    waterPan.add(waterTypeCB);
    waterPan.add(waterDetailsPan);
    
    updateWaterUI(); //set the correct details pan
    
    JPanel coastPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    coastPan.setBorder(new TitledBorder("Coast Outline"));
    coastPan.add(coastlineCB);
    coastPan.add(new JLabel("Thickness"));
    coastPan.add(coastlineThicknessSlider);
    coastPan.add(coastlineColorB);
    
    JPanel fooPan = new JPanel();
    fooPan.setLayout(new BoxLayout(fooPan, BoxLayout.X_AXIS));
    fooPan.add(waterPan);
    fooPan.add(coastPan);
    
    JPanel colorPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    colorPan.setBorder(new TitledBorder("Color Filter"));
    colorPan.add(colorFilterEnabledCB);
    colorPan.add(blackAndWhiteEnabledCB);
    colorPan.add(invertColorsEnabledCB);
    colorPan.add(new JLabel("Brightness"));
    colorPan.add(brightnessSlider);
    colorPan.add(new JLabel("Contrast"));
    colorPan.add(contrastSlider);
        
    // JPanel southPan = new JPanel(new GridLayout(3, 1));
    JPanel southPan = new JPanel();
    southPan.setLayout(new BoxLayout(southPan, BoxLayout.Y_AXIS));
    southPan.add(genParamsPan);
    southPan.add(sunPan);
    southPan.add(fooPan);
    southPan.add(colorPan);
    
    JPanel northPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    northPan.add(view3dB);
    northPan.add(view2dB);

    rootPan = new JPanel(new BorderLayout());
    rootPan.add(northPan, BorderLayout.NORTH);
    rootPan.add(canvas, BorderLayout.CENTER);
    rootPan.add(southPan, BorderLayout.SOUTH);
  }

  private void updateWaterUI() {
    Object type = waterTypeCB.getSelectedItem();
    waterDetailsPan.removeAll();
    if(type == WaterType.SIMPLE) {
      waterDetailsPan.add(new SimpleWaterPanel(app), BorderLayout.CENTER);
    } else if(type == WaterType.PURDY) {
      waterDetailsPan.add(new PurdyWaterPanel(app), BorderLayout.CENTER);
    }
    waterDetailsPan.revalidate();
    waterDetailsPan.repaint();
  }
  
  private void initComponenets() {
    
    TerrainGenerator tGen = app.getTerrainGenerator();
    SimplexNoiseGen nGen = tGen.getNoiseGenerator();
    
    //Noise
    octPan = new IntPanel("Oct", 2, nGen.getOctaves());
    roughPan = new DoublePanel("Rgh", 3, nGen.getRoughness());
    scalePan = new DoublePanel("Scale", 4, nGen.getScale());
    heightScalePan = new DoublePanel("Elv Scale", 3, tGen.getHeightScale());
    seedPan = new LongPanel("Seed", 15, nGen.getSeed());
    seedB = new JButton("Roll");
    
    //Terrain
    resolutionCB = new JComboBox<>(new Integer[] {256,512,1024,2048});
    resolutionCB.setSelectedItem(tGen.getSize());
    noiseMixPan = new DoublePanel("Noise Ratio", 3, tGen.getNoiseRatio());
    erodePan = new DoublePanel("Erode", 2, 0);
    updateB = new JButton("Update");
    

    Dimension sliderSize = new Dimension(80, new JSlider().getPreferredSize().height);
    
    //Shading
    sunSlider = new JSlider(0, 100, 30);
    sunSlider.setPreferredSize(sliderSize);
    sunSlider2 = new JSlider(0, 100, 30);
    sunSlider2.setPreferredSize(sliderSize);
    ambientSlider = new JSlider(0, 100, (int)(tGen.getAmbientLight() * 100));
    ambientSlider.setPreferredSize(sliderSize);
    
    List<ResourceEntry> hipTex = ResourceFinder.INST.findTextures("hipso_");
    hipsoCB = new JComboBox<>(hipTex.toArray(new ResourceEntry[hipTex.size()]));
    ResourceEntry sel = find(hipTex, tGen.getHipsoTex());
    if(sel != null) {
      hipsoCB.setSelectedItem(sel);
    }
    
    List<ResourceEntry> bathTex = ResourceFinder.INST.findTextures("bath_");
    bathCB = new JComboBox<>(bathTex.toArray(new ResourceEntry[bathTex.size()]));
    sel = find(bathTex, tGen.getBathTexture());
    if(sel != null) {
      bathCB.setSelectedItem(sel);
    }
    
    
    //Water
    waterLevelSlider = new JSlider(0, 100, (int)(app.getWaterLevel() * 100));
    waterLevelSlider.setPreferredSize(sliderSize);
    waterTypeCB = new JComboBox<>(WaterType.values());
    waterTypeCB.setSelectedItem(app.getWaterType());
    
    //Coastline
    coastlineCB = new JCheckBox("Enabled");
    coastlineCB.setSelected(tGen.isRenderCoastline());
    coastlineThicknessSlider = new JSlider(60, 100, (int)(tGen.getCoastlineThickness() * 100));
    coastlineThicknessSlider.setPreferredSize(sliderSize);
    com.jme3.math.Vector3f colf = tGen.getCoastlineColor();
    coastlineColorB = new ColorButton(new Color(colf.x, colf.y, colf.z));
    

    //Color Filter
    ColorFilter cf = app.getColorFilter();
    colorFilterEnabledCB = new JCheckBox("Enabled");
    colorFilterEnabledCB.setSelected(cf.isEnabled());
    blackAndWhiteEnabledCB = new JCheckBox("Black & White");
    blackAndWhiteEnabledCB.setSelected(cf.isBlackAndWhite());
    invertColorsEnabledCB = new JCheckBox("Invert Colors");
    invertColorsEnabledCB.setSelected(cf.isInvertColors());
    //map -1 to 1 to 0 to 100
    int selVal = (int)((cf.getBrightness() + 1)/2 * 100);
    brightnessSlider = new JSlider(0,100,selVal);
    brightnessSlider.setPreferredSize(sliderSize);
    selVal = (int)((cf.getContrast() + 1)/2 * 100);
    contrastSlider = new JSlider(0,100,selVal);
    contrastSlider.setPreferredSize(sliderSize);
    
    //Camera
    view3dB = new JToggleButton("3D");
    view2dB = new JToggleButton("2D");
    boolean is3d = app.getViewType() == ViewType.THREE_D;
    view3dB.setSelected(is3d);
    view2dB.setSelected(!is3d);
        
    ButtonGroup bg = new ButtonGroup();
    bg.add(view2dB);
    bg.add(view3dB);
    
    //Frame
    frame = new JFrame("Test");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
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
            SimplexNoiseGen nGen = app.getTerrainGenerator().getNoiseGenerator();
            Random r = new Random();
            nGen.setSeed(r.nextLong());
            seedPan.setVal(nGen.getSeed());
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
        updateWaterUI();

      }
      
    });
    
    hipsoCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            ResourceEntry sel = hipsoCB.getItemAt(hipsoCB.getSelectedIndex());
            app.getTerrainGenerator().setHipsoTexture(sel.resource);
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
            app.getTerrainGenerator().setBathTexture(sel.resource);
          }
        });

      }
    });
    
    coastlineCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.getTerrainGenerator().setRenderCoastline(coastlineCB.isSelected());
          }
        });

      }
    });
    
    coastlineThicknessSlider.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            float val = coastlineThicknessSlider.getValue() / (float)coastlineThicknessSlider.getMaximum();
            app.getTerrainGenerator().setCoastlineThickness(val);
          }
        });

      }
    });
    
    coastlineColorB.addColorListener(new ColorChangeListener() {
      
      @Override
      public void colorChanged(Color newColor) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.getTerrainGenerator().setCoastlineColor(TypeUtil.getColor3f(coastlineColorB.getColor()));
          }
        });
        
      }
    });
    
    colorFilterEnabledCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.getColorFilter().setEnabled(colorFilterEnabledCB.isSelected());
          }
        });

      }
    });
    
    blackAndWhiteEnabledCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.getColorFilter().setBlackAndWhite(blackAndWhiteEnabledCB.isSelected());
          }
        });

      }
    });
    
    invertColorsEnabledCB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {

        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.getColorFilter().setInvertColors(invertColorsEnabledCB.isSelected());
          }
        });

      }
    });
    
    contrastSlider.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            int selVal = contrastSlider.getValue();
            float val = (selVal/100f * 2) - 1;
            app.getColorFilter().setContrast(val);
          }
        });

      }
    });
    
    brightnessSlider.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            int selVal = brightnessSlider.getValue();
            float val = (selVal/100f * 2) - 1;
            app.getColorFilter().setBrightness(val);
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

    
    ambientSlider.addChangeListener(new ChangeListener() {

      @Override
      public void stateChanged(ChangeEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.getTerrainGenerator().setAmbientLight(ambientSlider.getValue() / 100f);
          }
        });

      }
    });
    
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
            app.canvasResized(canvas);
          }
        });
      }

    });
    
    view2dB.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.setViewType(ViewType.TWO_D);
          }
        });
        
      }
    });
    
    view3dB.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.setViewType(ViewType.THREE_D);
          }
        });
        
      }
    });

  }

  public WorldRenderer createApp() {
    AppSettings settings = new AppSettings(true);
    settings.setWidth(640);
    settings.setHeight(480);

    app = new WorldRenderer();

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
    
    TerrainGenerator tGen = app.getTerrainGenerator();
    
    tGen.setSize(resolutionCB.getItemAt(resolutionCB.getSelectedIndex()));
    
    tGen.setHeightScale((float) heightScalePan.getVal());
    
    tGen.setErodeFilter((float) erodePan.getVal());
    tGen.setNoiseRatio((float)noiseMixPan.getVal());
    
    SimplexNoiseGen gen = tGen.getNoiseGenerator();
    if(seedPan.getVal() != 0) {
      gen.setSeed(seedPan.getVal());
    }
    gen.setOctaves(octPan.getVal());
    gen.setRoughness(roughPan.getVal());
    gen.setScale(scalePan.getVal());
        
    app.updateTerrain();
  }

}
