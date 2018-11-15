package crap;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class GradientMaker {

  public static void main(String[] args) {
    GradientMaker gm = new GradientMaker();
    BufferedImage im = gm.createImage(64, 256);
    try {
      ImageIO.write(im, "png", new File("D:\\Dev\\temp\\bath2.png"));
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  
  ColorEntry[] entries = new ColorEntry[] {
//      new ColorEntry(226,240,241),
//      new ColorEntry(198,229,231),
//      new ColorEntry(170,208,219),
      new ColorEntry(143,195,217),
      new ColorEntry(115,179,207),
      new ColorEntry(95,169,204),
      new ColorEntry(75,159,195),
      new ColorEntry(56,148,185),
      new ColorEntry(43,138,166),
      new ColorEntry(42,130,152)
  };
  
  public GradientMaker() {
    double val = 0;
    double inc = 1d / (entries.length - 1);
    for(int i=0;i<entries.length;i++ ) {
      entries[i].val = val;
      val += inc;
    }
  }
  
  public BufferedImage createImage(int width, int height) {
    BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    
    Graphics g = res.getGraphics();
    
    double sampleVal = 0;
    double inc = 1d / (height - 1);
    
    for(int i=0;i<height;i++) {
      Color col = getColor(sampleVal);
      g.setColor(col);
      g.fillRect(0, i, width, 1);
      sampleVal += inc;
    }
    
    return res;
  }
  
  
  
  public Color getColor(double val) {
    
    if(val <= entries[0].val) {
      return entries[0].col;
    }
    
    //get the entry with a greater val than asked for
    int i = 0;
    double entVal = entries[i].val;
    while(val > entVal && i < entries.length -1) {
      i++;
      entVal = entries[i].val;
    }
    
    double aboveVal = entVal;
    double belowVal = entries[i - 1].val;
    
    //normalise
    double ratio = (val - belowVal) / (aboveVal - belowVal);
    
    int r = (int)(ratio * entries[i].col.getRed()) + (int)( (1 - ratio) * entries[i - 1].col.getRed());
    int g = (int)(ratio * entries[i].col.getGreen()) + (int)( (1 - ratio) * entries[i - 1].col.getGreen());
    int b = (int)(ratio * entries[i].col.getBlue()) + (int)( (1 - ratio) * entries[i - 1].col.getBlue());
        
    return new Color(clamp(r, 0, 255),clamp(g, 0, 255),clamp(b, 0, 255));
    
  }
  
  
  
  private int clamp(int input, int min, int max) {
    return (input < min) ? min : (input > max) ? max : input;
  }



  private class ColorEntry {
    
    double val;
    Color col;
    
    public ColorEntry(int r, int g, int b) {
      val = 0;
      col = new Color(r, g, b);
    }
    
    public ColorEntry(double val, Color col) {
      this.val = val;
      this.col = col;
    }
    
  }
  
}
