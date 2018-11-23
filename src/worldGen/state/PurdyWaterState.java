package worldGen.state;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.ColorRGBA;

import worldGen.render.PurdyWater;

public class PurdyWaterState implements Savable {

  public static final boolean DEF_ENABLED = true;
  public static final ColorRGBA DEF_WATER_COLOR = new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f);
  public static final ColorRGBA DEF_DEEP_WATER_COLOR = new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f);
  public static final float DEF_TRANSP = 0.12f;
  
  private boolean enabled;
  
  private ColorRGBA waterColor;
  private ColorRGBA deepWaterColor;

  private float transparency;
  
  public PurdyWaterState() {
    enabled = DEF_ENABLED;
    waterColor = DEF_WATER_COLOR;
    deepWaterColor = DEF_DEEP_WATER_COLOR;
    transparency = DEF_TRANSP;
  }
  
  public PurdyWaterState(PurdyWater pw) {
    enabled = pw.isEnabled();
    waterColor = pw.getWaterColor();
    deepWaterColor = pw.getDeepWaterColor();
    transparency = pw.getWaterTransparency();
  }
  
  public void apply(PurdyWater pw) {
    pw.setEnabled(enabled);
    pw.setWaterColor(waterColor);
    pw.setDeepWaterColor(deepWaterColor);
    pw.setWaterTransparency(transparency);
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule cap = ex.getCapsule(this);
    cap.write(enabled, "enabled", DEF_ENABLED);
    cap.write(waterColor, "waterColor", null);
    cap.write(deepWaterColor, "deepWaterColor", null);
    cap.write(transparency, "transparency", -1);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule cap = im.getCapsule(this);
    enabled = cap.readBoolean("enabled", DEF_ENABLED);
    waterColor = (ColorRGBA)cap.readSavable("waterColor", DEF_WATER_COLOR);
    deepWaterColor = (ColorRGBA)cap.readSavable("deepWaterColor", DEF_DEEP_WATER_COLOR);
    transparency = cap.readFloat("transparency", DEF_TRANSP);
  }
  
}
