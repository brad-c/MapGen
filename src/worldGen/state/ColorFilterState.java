package worldGen.state;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;

import worldGen.render.ColorFilter;

public class ColorFilterState implements Savable {

  public static final boolean DEF_ENABLED = false;
  public static final boolean DEF_B_AND_W = true;
  public static final boolean DEF_INV_COL = false;
  public static final float DEF_BRIGHTNESS = 0;
  public static final float DEF_CONTRAST = 0;
  
  private boolean enabled;
  
  private boolean isBlackAndWhite;
  private boolean isInvertColors;
  
  private float brightness;
  private float contrast;
  
  public ColorFilterState() {
    enabled = DEF_ENABLED;
    isBlackAndWhite = DEF_B_AND_W;
    isInvertColors = DEF_INV_COL;
    brightness = DEF_BRIGHTNESS;
    contrast = DEF_CONTRAST;
  }
  
  public ColorFilterState(ColorFilter cf) {
    enabled = cf.isEnabled();
    isBlackAndWhite =cf.isBlackAndWhite();
    isInvertColors = cf.isInvertColors();
    brightness = cf.getBrightness();
    contrast = cf.getContrast();
  }
  
  public void apply(ColorFilter cf) {
    cf.setEnabled(enabled);
    cf.setBlackAndWhite(isBlackAndWhite);
    cf.setInvertColors(isInvertColors);
    cf.setBrightness(brightness);
    cf.setContrast(contrast);
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule cap = ex.getCapsule(this);
    cap.write(enabled, "enabled", DEF_ENABLED);
    cap.write(isBlackAndWhite, "isBlackAndWhite", DEF_B_AND_W);
    cap.write(isInvertColors, "isInvertColors", DEF_INV_COL);
    cap.write(brightness, "brightness", -100);
    cap.write(contrast, "contrast", -100);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule cap = im.getCapsule(this);
    enabled = cap.readBoolean("enabled", DEF_ENABLED);
    isBlackAndWhite = cap.readBoolean("isBlackAndWhite", DEF_B_AND_W);
    isInvertColors = cap.readBoolean("isInvertColors", DEF_INV_COL);
    brightness = cap.readFloat("brightness", DEF_BRIGHTNESS);
    contrast = cap.readFloat("contrast", DEF_CONTRAST);
  }
  
}
