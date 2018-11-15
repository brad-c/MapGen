package crap;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Crap {

  public static void main(String[] args) {
    try {
      new Crap();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public Crap() throws Exception {
    BufferedImage im = ImageIO.read(new File("D:\\Dev\\temp\\circleGrad.png"));
    for(int i=0;i<im.getWidth();i++) {
      for(int j=0;j<im.getHeight();j++) {
        int rgb = im.getRGB(i, j);
        Color c = new Color(rgb);
//        System.out.print("" + c.getBlue() + ",");
      }
    }
    
    for(int i=0;i<256;i++) {
      System.out.print("0,");
    }
    
  }
  
  

}
