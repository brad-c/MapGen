package crap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import gui.ResourceFinder;

public class ImageHeightmapLoader {

  public static float[] loadGrayScaleData(String resource, float heightScale, boolean flipY) {

    BufferedImage im = null;
    InputStream in = ResourceFinder.INST.getResourceAsStream(resource);
    // if(in == null) {
    // File f = new File(resource);
    // if(f.exists()) {
    // try {
    // in = new FileInputStream(f);
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // }
    // }
    // }

    try {
      im = ImageIO.read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (im == null) {
      return null;
    }

    float[] res = new float[im.getWidth() * im.getHeight()];
    int index = 0;
    if (flipY) {

      for (int i = 0; i < im.getWidth(); i++) {
        for (int j = im.getHeight() - 1; j >= 0; j--) {
          int rgb = im.getRGB(i, j);
          Color c = new Color(rgb);
          res[index] = (c.getBlue() / 255f) * heightScale;
          index++;
        }
      }
      
    } else {
      for (int i = 0; i < im.getWidth(); i++) {
        for (int j = 0; j < im.getHeight(); j++) {
          int rgb = im.getRGB(i, j);
          Color c = new Color(rgb);
          res[index] = (c.getBlue() / 255f) * heightScale;
          index++;
        }
      }
    }

    return res;
  }

}
