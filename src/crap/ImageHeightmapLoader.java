package crap;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import worldGen.gui.ResourceFinder;

public class ImageHeightmapLoader {

  public static float[] loadGrayScaleData(String resource, int size, float heightScale, boolean flipY) {

    BufferedImage im = null;
    InputStream in = ResourceFinder.INST.getResourceAsStream(resource);
    if (in == null) {
      return null;
    }
    
    try {
      im = ImageIO.read(in);
    } catch (IOException e) {
      e.printStackTrace();
    }

    if (im == null) {
      return null;
    }

    
    if(im.getWidth() != size || im.getHeight() != size) {
      BufferedImage resized = new BufferedImage(size, size, im.getType());
      Graphics2D g = resized.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(im, 0, 0, size, size, 0, 0, im.getWidth(),
          im.getHeight(), null);
      g.dispose();
      im = resized;
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
