package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3f;

import gui.ResourceFinder.ResourceEntry;
import gui.widget.ColorButton;
import gui.widget.ColorButton.ColorChangeListener;
import render.ColorFilter;
import render.TerrainGenerator;
import render.WorldRenderer;
import render.WorldRenderer.WaterType;
import util.TypeUtil;

public class TerrainVisualsPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  private WorldRenderer app;
  // private TerrainGui terrainGui;

  // Shading
  private JSlider sunSlider;
  private JSlider sunSlider2;
  private JSlider ambientSlider;
  private JComboBox<ResourceFinder.ResourceEntry> hipsoCB;
  private JComboBox<ResourceFinder.ResourceEntry> bathCB;

  // Coastline
  private JCheckBox coastlineCB;
  private JSlider coastlineThicknessSlider;
  private ColorButton coastlineColorB;

  // Water
  private JComboBox<WaterType> waterTypeCB;
  private JPanel waterDetailsPan;

  // Color Filter
  private JCheckBox colorFilterEnabledCB;
  private JCheckBox blackAndWhiteEnabledCB;
  private JCheckBox invertColorsEnabledCB;
  private JSlider brightnessSlider;
  private JSlider contrastSlider;

  public TerrainVisualsPanel(TerrainGui terrainGui) {
    this.app = terrainGui.getWorldRenderer();
    initComponenets();
    //set all the defaults
    updateGUI(app);
    
    addComponents();
    addListeners();

    // We are pushing these values atm
    updateSunPos();
  }

  public void updateGUI(WorldRenderer ren) {
    this.app = ren;

    TerrainGenerator tGen = app.getTerrainGenerator();
    ambientSlider.setValue((int) (tGen.getAmbientLight() * 100));

    // TODO:
    List<ResourceEntry> hipTex = ResourceFinder.INST.findTextures("hipso_");
    ResourceEntry sel = find(hipTex, tGen.getHipsoTex());
    if (sel != null) {
      hipsoCB.setSelectedItem(sel);
    }
    List<ResourceEntry> bathTex = ResourceFinder.INST.findTextures("bath_");
    sel = find(bathTex, tGen.getBathTexture());
    if (sel != null) {
      bathCB.setSelectedItem(sel);
    }

    waterTypeCB.setSelectedItem(app.getWaterType());

    coastlineCB.setSelected(tGen.isRenderCoastline());
    coastlineThicknessSlider.setValue((int) (tGen.getCoastlineThickness() * 100));
    coastlineColorB.setColor(TypeUtil.getColorAWT(tGen.getCoastlineColor()));

    // Color Filter
    ColorFilter cf = app.getColorFilter();
    colorFilterEnabledCB.setSelected(cf.isEnabled());

    blackAndWhiteEnabledCB.setSelected(cf.isBlackAndWhite());
    invertColorsEnabledCB.setSelected(cf.isInvertColors());

    // map -1 to 1 to 0 to 100
    int selVal = (int) ((cf.getBrightness() + 1) / 2 * 100);
    brightnessSlider.setValue(selVal);

    selVal = (int) ((cf.getContrast() + 1) / 2 * 100);
    contrastSlider.setValue(selVal);
    
    updateWaterUI();
  }

  private void initComponenets() {

    TerrainGenerator tGen = app.getTerrainGenerator();

    Dimension sliderSize = new Dimension(80, new JSlider().getPreferredSize().height);
    // Shading
    sunSlider = new JSlider(0, 100, 30);
    sunSlider.setPreferredSize(sliderSize);
    sunSlider2 = new JSlider(0, 100, 30);
    sunSlider2.setPreferredSize(sliderSize);
    ambientSlider = new JSlider(0, 100, (int) (tGen.getAmbientLight() * 100));
    ambientSlider.setPreferredSize(sliderSize);

    List<ResourceEntry> hipTex = ResourceFinder.INST.findTextures("hipso_");
    hipsoCB = new JComboBox<>(hipTex.toArray(new ResourceEntry[hipTex.size()]));

    List<ResourceEntry> bathTex = ResourceFinder.INST.findTextures("bath_");
    bathCB = new JComboBox<>(bathTex.toArray(new ResourceEntry[bathTex.size()]));

    // Water
    waterDetailsPan = new JPanel(new BorderLayout());
    waterTypeCB = new JComboBox<>(WaterType.values());

    // Coastline
    coastlineCB = new JCheckBox("Enabled");
    coastlineThicknessSlider = new JSlider(60, 100, (int) (tGen.getCoastlineThickness() * 100));
    coastlineThicknessSlider.setPreferredSize(sliderSize);
    com.jme3.math.Vector4f colf = tGen.getCoastlineColor();
    coastlineColorB = new ColorButton(new Color(colf.x, colf.y, colf.z));

    // Color Filter
    colorFilterEnabledCB = new JCheckBox("Enabled");
    blackAndWhiteEnabledCB = new JCheckBox("Black & White");
    invertColorsEnabledCB = new JCheckBox("Invert Colors");

    brightnessSlider = new JSlider(0, 100);
    brightnessSlider.setPreferredSize(sliderSize);
    contrastSlider = new JSlider(0, 100);
    contrastSlider.setPreferredSize(sliderSize);
  }

  private ResourceEntry find(List<ResourceEntry> ents, String res) {
    for (ResourceEntry e : ents) {
      if (e.resource.equals(res)) {
        return e;
      }
    }
    return null;
  }

  private void addComponents() {
    // int gridx, int gridy, int gridwidth, int gridheight, double weightx, double
    // weighty, int anchor, int fill, Insets insets, int ipadx, int ipady
    Insets insets = new Insets(2, 2, 2, 2);

    JPanel texturePan = new JPanel(new GridBagLayout());
    texturePan.setBorder(new TitledBorder("Textures"));
    texturePan.add(new JLabel("Terrain"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    texturePan.add(hipsoCB, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    texturePan.add(new JLabel(" Water"), new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    texturePan.add(bathCB, new GridBagConstraints(1, 1, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

    JPanel lightingPan = new JPanel(new GridBagLayout());
    lightingPan.setBorder(new TitledBorder("Lighting"));
    lightingPan.add(new JLabel("Sun"), new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    lightingPan.add(sunSlider, new GridBagConstraints(1, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    lightingPan.add(sunSlider2, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    lightingPan.add(new JLabel("Ambient"), new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    lightingPan.add(ambientSlider, new GridBagConstraints(1, 2, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

    JPanel coastPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    coastPan.setBorder(new TitledBorder("Coast Outline"));
    coastPan.add(coastlineCB);
    coastPan.add(new JLabel("Thickness"));
    coastPan.add(coastlineThicknessSlider);
    coastPan.add(coastlineColorB);

    JPanel bcPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    bcPan.add(new JLabel("Brightness"));
    bcPan.add(brightnessSlider);
    bcPan.add(new JLabel("Contrast"));
    bcPan.add(contrastSlider);

    JPanel cPan = new JPanel(new GridBagLayout());
    cPan.setBorder(new TitledBorder("Color Fliter"));
    cPan.add(colorFilterEnabledCB, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    cPan.add(blackAndWhiteEnabledCB, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    cPan.add(invertColorsEnabledCB, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    cPan.add(bcPan, new GridBagConstraints(0, 1, 3, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));

    JPanel waterPan = new JPanel(new GridBagLayout());
    waterPan.setBorder(new TitledBorder("Water"));
    waterPan.add(waterTypeCB, new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    waterPan.add(waterDetailsPan, new GridBagConstraints(0, 1, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));
    updateWaterUI(); // set the correct details pan

    setLayout(new GridBagLayout());
    int y = 0;
    add(texturePan, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(lightingPan, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(coastPan, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(cPan, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(waterPan, new GridBagConstraints(0, y++, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    add(new JPanel(), new GridBagConstraints(0, y++, 1, 1, 1, 1, GridBagConstraints.WEST, GridBagConstraints.BOTH, insets, 0, 0));

  }

  private void addListeners() {

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
            float val = coastlineThicknessSlider.getValue() / (float) coastlineThicknessSlider.getMaximum();
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
            app.getTerrainGenerator().setCoastlineColor(TypeUtil.getColor4f(coastlineColorB.getColor()));
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
            float val = (selVal / 100f * 2) - 1;
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
            float val = (selVal / 100f * 2) - 1;
            app.getColorFilter().setBrightness(val);
          }
        });

      }
    });

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

  private void updateWaterUI() {
    Object type = waterTypeCB.getSelectedItem();
    waterDetailsPan.removeAll();
    if (type == WaterType.SIMPLE) {
      waterDetailsPan.add(new SimpleWaterPanel(app), BorderLayout.CENTER);
    } else if (type == WaterType.PURDY) {
      waterDetailsPan.add(new PurdyWaterPanel(app), BorderLayout.CENTER);
    }
    waterDetailsPan.revalidate();
    waterDetailsPan.repaint();
  }

}
