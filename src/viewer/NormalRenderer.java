package viewer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import vecmath.Vector3d;

public class NormalRenderer {

private static final int PIXEL_SCALE = 1;
private double waterHeight = -1;

public BufferedImage createImage(float[] hm, int width, int height) {

    int imHeight = height * PIXEL_SCALE;
    int imWdith = width * PIXEL_SCALE;

    BufferedImage result = new BufferedImage(imWdith, imHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = result.createGraphics();

    g2d.setColor(Color.white);
    g2d.fillRect(0, 0, imWdith, imHeight);

    Vector3d lightDir = new Vector3d(1,0,1);
    lightDir.normalize();
    
    NormalGenerator gen = new NormalGenerator();
    int index = 0;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        
        
        if(hm[index] < waterHeight) {
          fillColor(g2d, x, y, new Color(1f,1f,1f));
        } else {

          //TODO:!!!!!!!!!!!!!!!!!!!!!!!!!!
//          Vector3d norm = gen.getNormal(hm, x, y);
          Vector3d norm = new Vector3d(0,0,1);
//           System.out.print(norm);

          //float diff = (float) Math.abs(norm.dot(lightDir));
          float diff = (float) Math.max(norm.dot(lightDir), 0);
          float r = diff;
          float g = diff;
          float b = diff;
//           System.out.print(diff + ",");

          // float r = (float)normalise(norm.x, -1, 1);
          // float g = (float)normalise(norm.y, -1, 1);
          // float b = (float)normalise(norm.z, -1, 1);

          fillColor(g2d, x, y, new Color(r, g, b));
        }

      }
      index++;
//      System.out.println();
    }

    g2d.dispose();

    return result;
  }
  
  public BufferedImage shadeImage(BufferedImage color, BufferedImage lightMap) {
    if(color == null) {
      return null;
    }
    if(lightMap == null) {
      return color;
    }
    
    BufferedImage res = new BufferedImage(color.getWidth(), color.getHeight(), BufferedImage.TYPE_INT_RGB);
    for (int x = 0; x < color.getWidth(); x++) {
      for (int y = 0; y < color.getHeight(); y++) {
        Color col = new Color(color.getRGB(x, y));
        float lightVal = new Color(lightMap.getRGB(x, y)).getBlue()/255f;
        Color blendCol = new Color(shade(col.getRed(), lightVal), shade(col.getGreen(), lightVal), shade(col.getBlue(), lightVal));
        res.setRGB(x, y, blendCol.getRGB());
        //res.setRGB(x, y, col.getRGB());
      }
//      System.out.println("");
    }
    return res;
  }
  
  
  private int shade(int color, float lightVal) {
    int res = (int)(color*lightVal);
    return Math.min(res, 255);
  }

  public void setWaterHeight(double waterHeight) {
    this.waterHeight = waterHeight;
  }
  
//
//  vec3 norm = normalize(Normal);
//  vec3 lightDir = normalize(lightPos - FragPos);
//  float diff = max(dot(norm, lightDir), 0.0);
//  vec3 diffuse = diff * lightColor;
  
  private double normalise(double val, double min, double max) {
    double normalised = val + -min;
    max += -min;
    normalised /= max;
    return normalised;
  }
  
  private void fillColor(Graphics2D g2d, int x, int y, Color col) {
    g2d.setColor(col);
    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);
  }

  
}
