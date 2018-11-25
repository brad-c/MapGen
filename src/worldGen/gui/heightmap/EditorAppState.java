package worldGen.gui.heightmap;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public class EditorAppState  extends AbstractAppState {

  private Application app;
  private EditorController controller;

  public EditorAppState() {
  }

  /**
   * This is called by SimpleApplication during initialize().
   */
  void setController(EditorController cam) {
    this.controller = cam;
  }

  public EditorController getController() {
    return controller;
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
    super.initialize(stateManager, app);
    this.app = app;
    if (app.getInputManager() != null) {
      controller.registerWithInput();
    }
  }

  @Override
  public void setEnabled(boolean enabled) {
    super.setEnabled(enabled);
    controller.setEnabled(enabled);
  }

  @Override
  public void cleanup() {
    super.cleanup();
    if (app.getInputManager() != null) {
      controller.unregisterInput();
    }
  }

}