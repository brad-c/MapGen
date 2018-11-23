package worldGen.render;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;

public class OrthoCamAppState extends AbstractAppState {

  private Application app;
  private OrthoCameraController orthoCam;

  public OrthoCamAppState() {
  }

  /**
   *  This is called by SimpleApplication during initialize().
   */
  void setController( OrthoCameraController cam ) {
      this.orthoCam = cam;
  }
  
  public OrthoCameraController getController() {
      return orthoCam;
  }

  @Override
  public void initialize(AppStateManager stateManager, Application app) {
      super.initialize(stateManager, app);
      
      this.app = app;

      if (app.getInputManager() != null) {
      
//          if (orthoCam == null) {
//              orthoCam = new OrthoCameraController(app.getCamera());
//          }
          
          orthoCam.registerWithInput(app.getInputManager());
      }
  }
          
  @Override
  public void setEnabled(boolean enabled) {
      super.setEnabled(enabled);
      
      orthoCam.setEnabled(enabled);
  }
  
  @Override
  public void cleanup() {
      super.cleanup();

      if (app.getInputManager() != null) {
          orthoCam.unregisterInput();
      }
  }

}
