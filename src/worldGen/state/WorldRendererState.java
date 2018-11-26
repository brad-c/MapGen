package worldGen.state;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector4f;

import worldGen.render.CameraState;
import worldGen.render.WorldRenderer;
import worldGen.render.WorldRenderer.ViewType;
import worldGen.render.WorldRenderer.WaterType;

public class WorldRendererState implements Savable {

  public static final Vector4f DEF_WATER_COL = new Vector4f(5 / 255f, 36 / 255f, 78 / 255f, 1.0f);
//  public static final WaterType DEF_WATER_TYPE = WaterType.PURDY;
  public static final WaterType DEF_WATER_TYPE = WaterType.TERRAIN;
  public static final ViewType DEF_VIEW_TYPE = ViewType.THREE_D;
  public static final PurdyWaterState DEF_PURDY_WATER = new PurdyWaterState();
  public static final ColorFilterState DEF_COLOR_FILTER = new ColorFilterState();
  
  
  private ViewType viewType;
  private CameraState camState2d;
  private CameraState camState3d;
  
  private Vector4f simpleWaterColor;
  private WaterType waterType;
  private PurdyWaterState purdyWater;
  
  private ColorFilterState colorFilter;
  
  public WorldRendererState() {
    simpleWaterColor = DEF_WATER_COL;
    waterType = DEF_WATER_TYPE;
    viewType = DEF_VIEW_TYPE;
    purdyWater = DEF_PURDY_WATER;
    colorFilter = DEF_COLOR_FILTER;
  }
  
  public WorldRendererState(WorldRenderer wr) {
    camState2d = wr.getCameraState(WorldRenderer.ViewType.TWO_D);
    camState3d = wr.getCameraState(WorldRenderer.ViewType.THREE_D);
    simpleWaterColor = wr.getSimpleWaterColor();
    waterType = wr.getWaterType();
    viewType = wr.getViewType();
    purdyWater = new PurdyWaterState(wr.getPurdyWater());
    colorFilter = new ColorFilterState(wr.getColorFilter());
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
    //TODO: Purdy water needs to be applied before water type
    //as water type might change the enabled state of the purdy water
    if(purdyWater != null) {
      purdyWater.apply(wr.getPurdyWater());
    }
    if(waterType != null) {
      wr.setWaterType(waterType);
    }
    if(viewType != null) {
      wr.setViewType(viewType);
    }
    if(colorFilter != null) {
      colorFilter.apply(wr.getColorFilter());
    }
  }
  
  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule capsule = ex.getCapsule(this);
    capsule.write(camState2d, "camState2d", null);
    capsule.write(camState3d, "camState3d", null);
    capsule.write(viewType, "viewType", null);
    capsule.write(simpleWaterColor, "simpleWaterColor", null);
    capsule.write(waterType, "waterType", null);
    capsule.write(purdyWater, "purdyWater", null);
    capsule.write(colorFilter, "colorFilter", null);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule capsule = im.getCapsule(this);
    camState2d = (CameraState)capsule.readSavable("camState2d", null);
    camState3d = (CameraState)capsule.readSavable("camState3d", null);
    viewType = capsule.readEnum("viewType", ViewType.class, DEF_VIEW_TYPE);
    simpleWaterColor = (Vector4f)capsule.readSavable("simpleWaterColor", DEF_WATER_COL);
    waterType = capsule.readEnum("waterType", WaterType.class, DEF_WATER_TYPE);
    purdyWater = (PurdyWaterState)capsule.readSavable("purdyWater", DEF_PURDY_WATER);
    colorFilter = (ColorFilterState)capsule.readSavable("colorFilter", DEF_COLOR_FILTER);
  }

  public ViewType getViewType() {
    return viewType;
  }

  public void setViewType(ViewType viewType) {
    this.viewType = viewType;
  }

  public CameraState getCamState2d() {
    return camState2d;
  }

  public void setCamState2d(CameraState camState2d) {
    this.camState2d = camState2d;
  }

  public CameraState getCamState3d() {
    return camState3d;
  }

  public void setCamState3d(CameraState camState3d) {
    this.camState3d = camState3d;
  }

  public Vector4f getSimpleWaterColor() {
    return simpleWaterColor;
  }

  public void setSimpleWaterColor(Vector4f simpleWaterColor) {
    this.simpleWaterColor = simpleWaterColor;
  }

  public WaterType getWaterType() {
    return waterType;
  }

  public void setWaterType(WaterType waterType) {
    this.waterType = waterType;
  }

  public PurdyWaterState getPurdyWater() {
    return purdyWater;
  }

  public void setPurdyWater(PurdyWaterState purdyWater) {
    this.purdyWater = purdyWater;
  }

  public ColorFilterState getColorFilter() {
    return colorFilter;
  }

  public void setColorFilter(ColorFilterState colorFilter) {
    this.colorFilter = colorFilter;
  }
  
  

}

