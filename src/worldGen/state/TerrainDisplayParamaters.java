package worldGen.state;

import java.io.IOException;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

import worldGen.render.TerrainGenerator;

public class TerrainDisplayParamaters implements Savable {

  public static final String DEF_HIPSO_TEX = "textures/hipso_one.png";;
  public static final String DEF_BATH_TEX = "textures/bath_dark.png";
  public static final boolean DEF_DISCARD_WATER = false;
  public static final Vector3f DEF_SUN_DIR = new Vector3f(-0.1816357f, 0.80901706f, -0.5590169f).normalizeLocal();
  public static final float DEF_AMBIENT_LIGHT = 0.1f;
  public static final boolean DEF_RENDER_COASTLINE = false;
  public static final float DEF_COASTLINE_THICKNESS = 0.8f;
  public static final Vector4f DEF_COATLINE_COLOR = new Vector4f(0, 0, 0, 1);

  private String hipsoTex;
  private String bathTex;

  private Vector3f sunDir;
  private float ambientLight;

  private boolean renderCoastline;
  private float coastlineThickness;
  private Vector4f coastlineColor;

  private boolean discardWater;

  public TerrainDisplayParamaters() {
    hipsoTex = DEF_HIPSO_TEX;
    bathTex = DEF_BATH_TEX;
    sunDir = DEF_SUN_DIR;
    ambientLight = DEF_AMBIENT_LIGHT;
    renderCoastline = DEF_RENDER_COASTLINE;
    coastlineThickness = DEF_COASTLINE_THICKNESS;
    coastlineColor = DEF_COATLINE_COLOR;
    discardWater = DEF_DISCARD_WATER;
  }

  public TerrainDisplayParamaters(TerrainGenerator gen) {
    hipsoTex = gen.getHipsoTex();
    bathTex = gen.getBathTexture();
    sunDir = gen.getSunDirection();
    ambientLight = gen.getAmbientLight();
    renderCoastline = gen.isRenderCoastline();
    coastlineThickness = gen.getCoastlineThickness();
    coastlineColor = gen.getCoastlineColor();
    discardWater = gen.isDiscardWater();
  }

  public void apply(TerrainGenerator gen) {
    gen.setHipsoTexture(hipsoTex);
    gen.setBathTexture(bathTex);
    gen.setSunDirection(sunDir);
    gen.setAmbientLight(ambientLight);
    gen.setRenderCoastline(renderCoastline);
    gen.setCoastlineThickness(coastlineThickness);
    gen.setCoastlineColor(coastlineColor);
    gen.setDiscardWater(discardWater);
  }

  @Override
  public void write(JmeExporter ex) throws IOException {
    OutputCapsule cap = ex.getCapsule(this);
    cap.write(hipsoTex, "hipsoTex", null);
    cap.write(bathTex, "bathTex", null);
    cap.write(sunDir, "sunDir", null);
    cap.write(ambientLight, "ambientLight", -1);
    cap.write(renderCoastline, "renderCoastline", false);
    cap.write(coastlineThickness, "coastlineThickness", -1);
    cap.write(coastlineColor, "coastlineColor", null);
    cap.write(discardWater, "discardWater", false);
  }

  @Override
  public void read(JmeImporter im) throws IOException {
    InputCapsule cap = im.getCapsule(this);

    hipsoTex = cap.readString("hipsoTex", DEF_HIPSO_TEX);
    bathTex = cap.readString("bathTex", DEF_HIPSO_TEX);
    sunDir = (Vector3f)cap.readSavable("sunDir", DEF_SUN_DIR);
    ambientLight = cap.readFloat("ambientLight", DEF_AMBIENT_LIGHT);
    renderCoastline = cap.readBoolean("renderCoastline", DEF_RENDER_COASTLINE);
    coastlineThickness = cap.readFloat("coastlineThickness", DEF_COASTLINE_THICKNESS);
    coastlineColor = (Vector4f)cap.readSavable("coastlineColor", DEF_COATLINE_COLOR);
    discardWater = cap.readBoolean("discardWater", DEF_DISCARD_WATER);
    
  }

}
