package crap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import gui.ResourceFinder;

public class ImageHeightmapLoader {

  public static float[] loadGrayScaleData(String resource, float heightScale) {
    
    
    BufferedImage im = null;
    try {
      InputStream in = ResourceFinder.INST.getResourceAsStream(resource);
      im = ImageIO.read(in);
      
    } catch (IOException e) {
      e.printStackTrace();
    }
        
//    try {
//      InputStream in = ResourceFinder.INST.getResourceAsStream(resource);
//      im = ImageIO.read(new File(fileName));
//    } catch (IOException e) {
//      e.printStackTrace();
//      return new float[0];
//    }
    
    
    if(im == null) {
      return null;
    }
    
    int index = 0;
    float[] res = new float[im.getWidth() * im.getHeight()];
    for(int i=0;i<im.getWidth();i++) {
      for(int j=0;j<im.getHeight();j++) {
        int rgb = im.getRGB(i, j);
        Color c = new Color(rgb);
        res[index] = (c.getBlue() / 255f) * heightScale;
        index++;
      }
      
    }
    
    return res;
  }
  
}
