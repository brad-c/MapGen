package worldGen.gui.heightmap;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import com.jme3.app.state.AppStateManager;

import worldGen.gui.TerrainGui;
import worldGen.render.WorldRenderer;

public class EditorPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private WorldRenderer world;
    
  private EditorController controller;

  private EditorAppState appState;
  
  private AdjustHeightOperation adjustHeightOp;
  private JToggleButton incHeightB;
  private JSlider incHeightRadSlider;
  private JSlider incHeightSpeedSlider;
  

  public EditorPanel(TerrainGui terrainGui) {
 
    world = terrainGui.getWorldRenderer();
    controller = new EditorController(this);
    
    appState = new EditorAppState();
    appState.setController(controller);
    
    adjustHeightOp = new AdjustHeightOperation(world);
    
    initComponents();
    addComponents();
    addListeners();
  }
  
  public EditorController geEditorController() {
    return controller;
  }

  public WorldRenderer getWorld() {
    return world;
  }
  
  public void updateGUI(WorldRenderer ren) {
    world.getHeightMapEditor().updateLocalTerrain();
  }

  public void setActive(boolean isActive) {
    AppStateManager sm = world.getStateManager();
    if(isActive) {
      if(!sm.hasState(appState)) {
        sm.attach(appState);
      }
    } else {
      if(sm.hasState(appState)) {
        sm.detach(appState);
      }
      incHeightB.setSelected(false);
    }
    appState.setEnabled(isActive);
    
    incHeightB.setEnabled(isActive);
    incHeightRadSlider.setEnabled(isActive);
    incHeightSpeedSlider.setEnabled(isActive);
    
    if(!isActive) {
      world.setCameraControlEnabled(true);
    }
    controller.setEnabled(isActive);
    updateControlls();
  }
  
  public void cancelAction() {
    incHeightB.setSelected(false);
    controller.setOperation(null);
  }

  private void initComponents() {
    incHeightB = new JToggleButton("Adjust");
    incHeightB.setEnabled(false);
    incHeightB.setToolTipText("Left click to raise terrain, right click to lower");
    
    Dimension sliderSize = new Dimension(20, new JSlider().getPreferredSize().height);
    
    incHeightRadSlider = new JSlider(10,100);
    incHeightRadSlider.setValue((int)(adjustHeightOp.getRadius() * 100));
    incHeightRadSlider.setEnabled(false);
    incHeightRadSlider.setPreferredSize(sliderSize);
    
    incHeightSpeedSlider = new JSlider(1,100);
    incHeightSpeedSlider.setValue((int)(adjustHeightOp.getSpeed() * 100));
    incHeightSpeedSlider.setEnabled(false);
    incHeightSpeedSlider.setPreferredSize(sliderSize);
  }

  private void addComponents() {
    
    Insets insets = new Insets(2, 2, 2, 2);
    JPanel incPan = new JPanel(new GridBagLayout());
    incPan.add(incHeightB, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    incPan.add(new JLabel("Radius: "), new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    incPan.add(incHeightRadSlider, new GridBagConstraints(2, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    incPan.add(new JLabel("Speed: "), new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));
    incPan.add(incHeightSpeedSlider, new GridBagConstraints(4, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));
    incPan.add(new JPanel(),  new GridBagConstraints(0, 1, 1, 1, 0, 1, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, insets, 0, 0));
    
    setLayout(new BorderLayout());
    add(incPan, BorderLayout.CENTER);
  }

  private void addListeners() {
    incHeightB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        updateControlls();
      }

    });
    
    incHeightRadSlider.addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        adjustHeightOp.setRadius(incHeightRadSlider.getValue() / 100f);
      }
    });
    
    incHeightSpeedSlider.addChangeListener(new ChangeListener() {
      
      @Override
      public void stateChanged(ChangeEvent e) {
        adjustHeightOp.setSpeed(incHeightSpeedSlider.getValue() / 100f);
      }
    });

  }

  private void updateControlls() {
    world.enqueue(new Runnable() {
      @Override
      public void run() {
        if(incHeightB.isSelected()) {
          controller.setOperation(adjustHeightOp);
        } else {
          controller.setOperation(null);
        }
        world.setCameraControlEnabled(!incHeightB.isSelected());
      }
    });
  }

  public void onSave() {
    Future<Object> f = world.enqueue(new Callable<Object>() {
      
      @Override
      public Object call()  {
        world.getHeightMapEditor().applyChanges();
        return null;
      }
    });
    try {
      f.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }
    
  }

  

}
