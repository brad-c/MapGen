package project;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import render.WorldRenderer;

public class WorldGenProject implements Savable {
  
  private WorldRendererState worldRenderState;
  
  public WorldGenProject() {
    worldRenderState = new WorldRendererState();
  }
  
  public WorldGenProject(WorldRenderer wr) {
    worldRenderState = new WorldRendererState(wr);
  }
  
  public void apply(WorldRenderer wr) {
    if(worldRenderState != null) {
      worldRenderState.apply(wr);
    }
  }
  
  
  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(worldRenderState, "worldRenderState", null);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    worldRenderState = (WorldRendererState)capsule.readSavable("worldRenderState", null);
  }

  
  
}
