package viewer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

public class HeightMapRenderer {

  private static final int PIXEL_SCALE = 1;
  
  private double waterHeight = 0.5;

  public BufferedImage createImage(float[] hm, int width, int height) {
    return createImage(hm, width, height, 1);
  }

  
  
  public BufferedImage createImage(float[] hm, int width, int height, float heightScale) {
    int imageHeight = height * PIXEL_SCALE;
    int imageWidth = width * PIXEL_SCALE;

    BufferedImage result = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
    Graphics2D g2d = result.createGraphics();

    g2d.setColor(Color.white);
    g2d.fillRect(0, 0, imageWidth, imageHeight);

    int index = 0;
    for (int x = 0; x < width; x++) {
      for (int y = 0; y < height; y++) {
        colorWater(g2d, x, y, hm[index] / heightScale);
//        colorGS(g2d, x, y, hm[index]);
        index++;
      }
    }

    g2d.dispose();

    return result;
  }

  private void colorWater(Graphics2D g2d, int x, int y, double val) {
    if(val < waterHeight) {
      fillColor(g2d, x, y, new Color(0,0,120));
    } else {
      val = normalise(val, waterHeight, 1);
      //colorGS(g2d, x, y, val);
      colorHipso(g2d, x, y, val);
    }
    
  }
  

  private double normalise(double val, double min, double max) {
    double normalised = val + -min;
    max += -min;
    normalised /= max;
    return normalised;
  }
  
  private void colorHipso(Graphics2D g2d, int x, int y, double val) {
    HipsoSampler hs= new HipsoSampler();
    fillColor(g2d, x, y, hs.getColor(val));
  }
  
  private void colorGS(Graphics2D g2d, int x, int y, double val) {
    fillColor(g2d, x, y, new Color((float)val, (float)val, (float)val));
  }

  private void fillColor(Graphics2D g2d, int x, int y, Color col) {
    g2d.setColor(col);
    g2d.fillRect(y * PIXEL_SCALE, x * PIXEL_SCALE, PIXEL_SCALE, PIXEL_SCALE);
  }

  public double getWaterHeight() {
    return waterHeight;
  }

  public void setWaterHeight(double waterHeight) {
    this.waterHeight = waterHeight;
  }

  
  
  
  

}
