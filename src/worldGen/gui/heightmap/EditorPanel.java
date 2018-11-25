package worldGen.gui.heightmap;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.JToggleButton;

import com.jme3.app.state.AppStateManager;

import worldGen.gui.TerrainGui;
import worldGen.render.WorldRenderer;

public class EditorPanel extends JPanel {

  private static final long serialVersionUID = 1L;

  private WorldRenderer world;

  private JToggleButton incHeightB;

  private EditorController controller;

  private EditorAppState appState;

  public EditorPanel(TerrainGui terrainGui) {

    world = terrainGui.getWorldRenderer();
    controller = new EditorController(world);
    
    appState = new EditorAppState();
    appState.setController(controller);
        
    initComponents();
    addComponents();
    addListeners();
  }
  
  public EditorController geEditorController() {
    return controller;
  }

  public void updateGUI(WorldRenderer ren) {

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
    }
    appState.setEnabled(isActive);
    incHeightB.setEnabled(isActive);
    
    if(!isActive) {
      world.setCameraControlEnabled(true);
    }
    controller.setEnabled(isActive);
  }

  private void initComponents() {
    incHeightB = new JToggleButton("Inc Height");
    incHeightB.setEnabled(false);
  }

  private void addComponents() {
    setLayout(new FlowLayout(FlowLayout.RIGHT));
    add(incHeightB);
  }

  private void addListeners() {
    incHeightB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        controller.setIncHeight(incHeightB.isSelected());
        world.setCameraControlEnabled(!incHeightB.isSelected());
      }

    });

  }

}
