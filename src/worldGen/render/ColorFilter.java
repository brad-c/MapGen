package worldGen.render;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.FastMath;
import com.jme3.post.Filter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;

import worldGen.state.ColorFilterState;

public class ColorFilter extends Filter {

  private boolean isBlackAndWhite;
  private boolean isInvertColors;
  
  //Value between -1 and 1
  private float brightness;
  private float contrast;
  
  public ColorFilter() {
    super("ColorFilter");
    new ColorFilterState().apply(this);
  }

  @Override
  protected void initFilter(AssetManager manager, RenderManager renderManager, ViewPort vp, int w, int h) {
    material = new Material(manager, "materials/colorFilter.j3md");
    setBlackAndWhite(isBlackAndWhite());
    setInvertColors(isInvertColors());
    setBrightness(getBrightness());
    setContrast(getContrast());
  }

  @Override
  protected Material getMaterial() {
    return material;
  }

  public boolean isBlackAndWhite() {
    return isBlackAndWhite;
  }

  public void setBlackAndWhite(boolean isBlackAndWhite) {
    this.isBlackAndWhite = isBlackAndWhite;
    if(material != null) {
      material.setBoolean("IsBlackAndWhite", isBlackAndWhite());
    }
  }

  public boolean isInvertColors() {
    return isInvertColors;
  }

  public void setInvertColors(boolean isInvertColors) {
    this.isInvertColors = isInvertColors;
    if(material != null) {
      material.setBoolean("IsInvertColors", isInvertColors);
    }
  }

  public float getBrightness() {
    return brightness;
  }

  public void setBrightness(float brightness) {
    brightness = FastMath.clamp(brightness, -1, 1);
    this.brightness = brightness;
    if(material != null) {
      material.setFloat("Brightness", brightness);
    }
  }

  public float getContrast() {
    return contrast;
  }

  public void setContrast(float contrast) {
    contrast = FastMath.clamp(contrast, -1, 1);
    this.contrast = contrast;
    if(material != null) {
      material.setFloat("Contrast", contrast);
    }
  }

}
