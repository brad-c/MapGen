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
  private TerrainParameters terrainParams;
  
  public WorldGenProject() {
    worldRenderState = new WorldRendererState();
    terrainParams = new TerrainParameters();
  }
  
  public WorldGenProject(WorldRenderer wr) {
    worldRenderState = new WorldRendererState(wr);
    terrainParams = new TerrainParameters(wr.getTerrainGenerator());
  }
  
  public void apply(WorldRenderer wr) {
    if(worldRenderState != null) {
      worldRenderState.apply(wr);
    }
    if(terrainParams != null) {
      terrainParams.apply(wr.getTerrainGenerator());
    }
  }
  
  
  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(worldRenderState, "worldRenderState", null);
    capsule.write(terrainParams, "terrainParams", null);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    worldRenderState = (WorldRendererState)capsule.readSavable("worldRenderState", null);
    terrainParams = (TerrainParameters)capsule.readSavable("terrainParams", null);
  }

}
