package worldGen.util;

import java.awt.Color;
import java.io.File;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;

public class TypeUtil {

  public static Vector3f getColor3f(Color color) {
    return new Vector3f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
  }

  public static Vector4f getColor4f(Color color) {
    return new Vector4f(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f, color.getAlpha() / 255f);
  }

  public static ColorRGBA getColorRGBA(Color color) {
    Vector4f v = getColor4f(color);
    return new ColorRGBA(v.x, v.y, v.z, v.w);
  }

  public static ColorRGBA getColorRGBA(Vector4f v) {
    return new ColorRGBA(v.x, v.y, v.z, v.w);
  }

  public static Vector4f getColor4f(ColorRGBA waterColor) {
    return new Vector4f(waterColor.r, waterColor.g, waterColor.b, waterColor.a);
  }

  public static Vector3f getColor3f(ColorRGBA waterColor) {
    return new Vector3f(waterColor.r, waterColor.g, waterColor.b);
  }

  public static Color getColorAWT(Vector4f coastlineColor) {
    return new Color(coastlineColor.x, coastlineColor.y, coastlineColor.z, coastlineColor.w);
  }

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }
    return ext;
  }
}
