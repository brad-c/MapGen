package worldGen.state;

import java.io.File;

public class SaveLoadContext {

  
  public static final SaveLoadContext INSTANCE = new SaveLoadContext();
  
  private File currentFile;
  private boolean saveBinaryDataExternally = true;
  private String projectName;
  
  private SaveLoadContext() {
  }

  public File getCurrentFile() {
    return currentFile;
  }

  public void setCurrentFile(File currentFile) {
    this.currentFile = currentFile;
  }

  public boolean isSaveBinaryDataExternally() {
    return saveBinaryDataExternally;
  }

  public void setSaveBinaryDataExternally(boolean saveBinaryDataExternally) {
    this.saveBinaryDataExternally = saveBinaryDataExternally;
  }

  public String getProjectName() {
    return projectName;
  }

  public void setProjectName(String projectName) {
    this.projectName = projectName;
  }
  
}
