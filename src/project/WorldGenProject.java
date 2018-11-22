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
  private TerrainGenerationParameters terrainGenerationParameters;
  private TerrainDisplayParamaters terrainDisplayParamaters;
  
  public WorldGenProject() {
    worldRenderState = new WorldRendererState();
    terrainGenerationParameters = new TerrainGenerationParameters();
    terrainDisplayParamaters = new TerrainDisplayParamaters();
  }
  
  public WorldGenProject(WorldRenderer wr) {
    worldRenderState = new WorldRendererState(wr);
    terrainGenerationParameters = new TerrainGenerationParameters(wr.getTerrainGenerator());
    terrainDisplayParamaters = new TerrainDisplayParamaters(wr.getTerrainGenerator());
  }
  
  public void apply(WorldRenderer wr) {
    if(worldRenderState != null) {
      worldRenderState.apply(wr);
    }
    if(terrainGenerationParameters != null) {
      terrainGenerationParameters.apply(wr.getTerrainGenerator());
    }
    if(terrainDisplayParamaters != null) {
      terrainDisplayParamaters.apply(wr.getTerrainGenerator());
    }
  }
  
  
  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(worldRenderState, "worldRenderState", null);
    capsule.write(terrainGenerationParameters, "terrainGenerationParameters", null);
    capsule.write(terrainDisplayParamaters, "terrainDisplayParamaters", null);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    worldRenderState = (WorldRendererState)capsule.readSavable("worldRenderState", null);
    terrainGenerationParameters = (TerrainGenerationParameters)capsule.readSavable("terrainGenerationParameters", null);
    terrainDisplayParamaters= (TerrainDisplayParamaters)capsule.readSavable("terrainDisplayParamaters", null);
  }

}
