package worldGen.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import worldGen.gen.SimplexNoiseGen;
import worldGen.gui.ramp.TerrainElevationRampEditor;
import worldGen.gui.widget.DoublePanel;
import worldGen.gui.widget.IntPanel;
import worldGen.gui.widget.LongPanel;
import worldGen.render.TerrainGenerator;
import worldGen.render.WorldRenderer;

public class TerrainParamatersPanel extends JPanel {
  
  private static final long serialVersionUID = 1L;
  
  private WorldRenderer app;
  private TerrainGui terrainGui;

  // Noise
  private IntPanel octPan;
  private DoublePanel roughPan;
  private DoublePanel scalePan;
  private DoublePanel erodePan;
  private LongPanel seedPan;

  // Terrain
  private JComboBox<Integer> resolutionCB;
  private DoublePanel noiseMixPan;
  private DoublePanel heightScalePan;
  private JButton updateB;
  private JButton seedB;
  private JButton landRampB;
  private JSlider waterLevelSlider;

  public TerrainParamatersPanel(TerrainGui terrainGui) {
    this.terrainGui = terrainGui;
    this.app = terrainGui.getWorldRenderer();
    initComponenets();
    updateGUI(app);
    addComponents();
    addListeners();
    
    //TODO: We need to push the water level value to the terrain atm
    updateWaterLevel();
  }

  public void updateGUI(WorldRenderer ren) {
    TerrainGenerator tGen = app.getTerrainGenerator();
    SimplexNoiseGen nGen = tGen.getNoiseGenerator();
    octPan.setValue(nGen.getOctaves());
    roughPan.setValue(nGen.getRoughness());
    scalePan.setValue( nGen.getScale());
    heightScalePan.setValue(tGen.getHeightScale());
    seedPan.setVal(nGen.getSeed());
    resolutionCB.setSelectedItem(tGen.getSize());
    noiseMixPan.setValue(tGen.getNoiseRatio());
    waterLevelSlider.setValue( (int) (app.getWaterLevel() * 100));
    //TODO;
//    erodePan.setValue();
  }
  
  private void initComponenets() {
    TerrainGenerator tGen = app.getTerrainGenerator();
    SimplexNoiseGen nGen = tGen.getNoiseGenerator();

    // Noise
    octPan = new IntPanel("Oct", 2, nGen.getOctaves());
    roughPan = new DoublePanel("Rgh", 3, nGen.getRoughness());
    scalePan = new DoublePanel("Scale", 4, nGen.getScale());
    heightScalePan = new DoublePanel("Elv Scale", 3, tGen.getHeightScale());
    seedPan = new LongPanel("Seed", 15, nGen.getSeed());
    seedB = new JButton("Roll");

    // Terrain
    resolutionCB = new JComboBox<>(new Integer[] { 256, 512, 1024, 2048 });
    noiseMixPan = new DoublePanel("Noise Ratio", 3, tGen.getNoiseRatio());
    erodePan = new DoublePanel("Smooth", 2, 0);
    updateB = new JButton("Update");
    landRampB = new JButton("...");
    
    waterLevelSlider = new JSlider(0, 100);
    waterLevelSlider.setPreferredSize(new Dimension(120, new JSlider().getPreferredSize().height));
  }

  private void addComponents() {
        
    Insets insets = new Insets(0, 0, 0, 0);
    
    //Noise
    JPanel line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    line1.add(seedPan);
    line1.add(seedB);
    
    JPanel line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    line2.add(octPan);
    line2.add(roughPan);
    line2.add(scalePan);
    
    JPanel noisePan = new JPanel(new GridBagLayout());
    noisePan.setBorder(new TitledBorder("Noise"));
    noisePan.add(line1, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,insets,0,0));
    noisePan.add(line2, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,insets,0,0));
    
    //Terrain
    line1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    line1.add(heightScalePan);
    line1.add(erodePan);
    line1.add(noiseMixPan);
    
    line2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    line2.add(new JLabel("Elevation Ramp"));
    line2.add(landRampB);
    line2.add(new JLabel("Resolution"));
    line2.add(resolutionCB);
    
    JPanel line3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
    line3.add(new JLabel("Water Level"));
    line3.add(waterLevelSlider);
    
    JPanel terrainPan = new JPanel(new GridBagLayout());
    terrainPan.setBorder(new TitledBorder("Terrain"));
    terrainPan.add(line1, new GridBagConstraints(0,0,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,insets,0,0));
    terrainPan.add(line3, new GridBagConstraints(0,1,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,insets,0,0));
    terrainPan.add(line2, new GridBagConstraints(0,2,1,1,1,0,GridBagConstraints.WEST,GridBagConstraints.NONE,insets,0,0));
    
    
    JPanel updatePan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    updatePan.add(updateB);

    setLayout(new GridBagLayout());
    add(noisePan, new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0));
    add(terrainPan, new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.WEST,GridBagConstraints.HORIZONTAL,insets,0,0));
    add(updateB, new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(4,4,4,4),0,0));
    add(new JPanel(), new GridBagConstraints(0,3,1,1,1,1,GridBagConstraints.WEST,GridBagConstraints.BOTH,insets,0,0));
  }

  private void addListeners() {

    landRampB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        TerrainElevationRampEditor.showEditorFrame(terrainGui.getFrame(), app);
      }
    });

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

  private void updateWaterLevel() {
    int val = waterLevelSlider.getValue();
    float ratio = val / 100f;
    app.setWaterLevel(ratio);
  }
  
  public void updateTerrain() {

    TerrainGenerator tGen = app.getTerrainGenerator();

    tGen.setSize(resolutionCB.getItemAt(resolutionCB.getSelectedIndex()));

    tGen.setHeightScale((float) heightScalePan.getVal());

    tGen.setErodeFilter((float) erodePan.getVal());
    tGen.setNoiseRatio((float) noiseMixPan.getVal());

    SimplexNoiseGen gen = tGen.getNoiseGenerator();
    if (seedPan.getVal() != 0) {
      gen.setSeed(seedPan.getVal());
    }
    gen.setOctaves(octPan.getVal());
    gen.setRoughness(roughPan.getVal());
    gen.setScale(scalePan.getVal());

    app.updateTerrain();
  }

 

}
