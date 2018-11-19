package render;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.texture.Texture2D;
import com.jme3.water.WaterFilter;

public class PurdyWater {

  
  private WaterFilter waterFilter;
  private boolean enabled = true;
  
  private ColorRGBA waterColor = new ColorRGBA().setAsSrgb(0.0078f, 0.3176f, 0.5f, 1.0f);
  private ColorRGBA deepWaterColor  = new ColorRGBA().setAsSrgb(0.0039f, 0.00196f, 0.145f, 1.0f);

  private float waterTransparency = 0.12f;
  
  private transient WorldRenderer renderer;
  
  public PurdyWater() {
    
  }
  
  public void init(WorldRenderer renderer) {
    this.renderer = renderer;
  
    waterFilter = new WaterFilter(renderer.getRootNode(), renderer.getTerrainGenerator().getSunDirection());
    waterFilter.setUnderWaterFogDistance(80);
    
    waterFilter.setFoamHardness(0.3f);
    waterFilter.setReflectionDisplace(50);
    waterFilter.setRefractionConstant(0.25f);
    waterFilter.setColorExtinction(new Vector3f(30, 50, 70));
    waterFilter.setCausticsIntensity(0.4f);
    waterFilter.setWaveScale(0.003f);
//    waterFilter.setMaxAmplitude(2f);
    waterFilter.setMaxAmplitude(0f);
    waterFilter.setFoamTexture((Texture2D) renderer.getAssetManager().loadTexture("Common/MatDefs/Water/Textures/foam2.jpg"));
    waterFilter.setRefractionStrength(0.2f);
 // waterFilter.setWaveScale(0.001f);
    
    waterFilter.setWaveScale(0.0f);
    waterFilter.setFoamIntensity(0.2f);
        
    waterFilter.setLightColor(ColorRGBA.White);
    
    setWaterTransparency(getWaterTransparency());
    setWaterColor(getWaterColor());
    setDeepWaterColor(getDeepWaterColor());
    
    updateWaterFilter();
  }
  
  public boolean isEnabled() {
    return enabled;
  }
  
  public WaterFilter getWaterFilter() {
    return waterFilter;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
    if(waterFilter != null) {
      waterFilter.setEnabled(enabled);
    }
  }
      
  public ColorRGBA getWaterColor() {
    return waterColor;
  }
 
  public void setWaterColor(ColorRGBA waterColor) {
    this.waterColor = waterColor;
    if(waterFilter != null) {
      waterFilter.setWaterColor(waterColor);
    }
    
   renderer.getViewPort().setBackgroundColor(ColorRGBA.White);
  }

  public ColorRGBA getDeepWaterColor() {
    return deepWaterColor;
  }

  public void setDeepWaterColor(ColorRGBA deepWaterColor) {
    this.deepWaterColor = deepWaterColor;
    if(waterFilter != null) {
      waterFilter.setDeepWaterColor(deepWaterColor);
    }
  }

  public WorldRenderer getRenderer() {
    return renderer;
  }

  public float getWaterTransparency() {
    return waterTransparency;
  }

  public void setWaterTransparency(float waterTransparency) {
    this.waterTransparency = waterTransparency;
    if(waterFilter != null) {
      waterFilter.setWaterTransparency(waterTransparency);
    }
  }

  public void updateWaterFilter() {
    if(waterFilter == null) {
      return;
    }
    
    waterFilter.setWaterHeight(renderer.getWaterHeight());
    
    float rs = renderer.getTerrainGenerator().getRenderScale();
    float startFade = Math.max(0.3f, 0.6f * rs);
    float endFade = 6f * rs;
    waterFilter.setFoamExistence(new Vector3f(startFade, endFade, 1f));
   
    //Blueify the water at depth
    float colorExtinction = renderer.getWaterHeight();
    waterFilter.setColorExtinction(new Vector3f(colorExtinction / 3f, colorExtinction / 2f, colorExtinction));
  }
 
}
