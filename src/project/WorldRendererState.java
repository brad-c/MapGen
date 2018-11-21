package project;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector4f;

import render.CameraState;
import render.WorldRenderer;
import render.WorldRenderer.ViewType;
import render.WorldRenderer.WaterType;

public class WorldRendererState implements Savable {

  public static final Vector4f DEF_WATER_COL = new Vector4f(5 / 255f, 36 / 255f, 78 / 255f, 1.0f);
  public static final WaterType DEF_WATER_TYPE = WaterType.PURDY;
  public static final ViewType DEF_VIEW_TYPE = ViewType.THREE_D;
  
  private Vector4f simpleWaterColor;
  
  private WaterType waterType;
  
  private ViewType viewType;
  private CameraState camState2d;
  private CameraState camState3d;
  
  public WorldRendererState() {
    simpleWaterColor = DEF_WATER_COL;
    waterType = DEF_WATER_TYPE;
    viewType = DEF_VIEW_TYPE;
  }
  
  public WorldRendererState(WorldRenderer wr) {
    camState2d = wr.getCameraState(WorldRenderer.ViewType.TWO_D);
    camState3d = wr.getCameraState(WorldRenderer.ViewType.THREE_D);
    simpleWaterColor = wr.getSimpleWaterColor();
    waterType = wr.getWaterType();
    viewType = wr.getViewType();
  }
  
  public void apply(WorldRenderer wr) {
    if(camState2d != null) {
      wr.setCameraState(ViewType.TWO_D, camState2d);
    }
    if(camState3d != null) {
      wr.setCameraState(ViewType.THREE_D, camState3d);
    }
    if(simpleWaterColor != null) {
      wr.setSimpleWaterColor(simpleWaterColor);
    }
    if(waterType != null) {
      wr.setWaterType(waterType);
    }
    if(viewType != null) {
      wr.setViewType(viewType);
    }
  }
  
  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(camState2d, "camState2d", null);
    capsule.write(camState3d, "camState3d", null);
    capsule.write(viewType, "viewType", null);
    capsule.write(simpleWaterColor, "simpleWaterColor", DEF_WATER_COL);
    capsule.write(waterType, "waterType", DEF_WATER_TYPE);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    camState2d = (CameraState)capsule.readSavable("camState2d", null);
    camState3d = (CameraState)capsule.readSavable("camState3d", null);
    viewType = capsule.readEnum("viewType", ViewType.class, DEF_VIEW_TYPE);
    simpleWaterColor = (Vector4f)capsule.readSavable("simpleWaterColor", DEF_WATER_COL);
    waterType = capsule.readEnum("waterType", WaterType.class, DEF_WATER_TYPE);
  }

}

