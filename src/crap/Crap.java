package crap;

import java.awt.image.BufferedImage;
import java.net.URL;

import javax.imageio.ImageIO;

public class Crap {

  public static void main(String[] args) {

//    ResourceFinder.INST.findEntries(path, prefix, postFix)
    
    try {
//      URL url = Thread.currentThread().getContextClassLoader().getResource("resources/icons/globe.png");
      URL url = Thread.currentThread().getContextClassLoader().getResource("icons/globe.png");
      System.out.println("Crap.main: " + url);
//      BufferedImage bi = ImageIO.read( ClassLoader.getSystemResource( "resources/icons/globe.png" ) );
      BufferedImage bi = ImageIO.read( url );
      
      
//      ImageIcon im = new ImageIcon(bi);
     
    } catch (Exception e) {
      e.printStackTrace();
    }
    
  }

}
