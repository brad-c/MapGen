package viewer;

import java.awt.Color;

import vecmath.VecmathUtil;

public class HipsoSampler {

  
  ColorEntry[] entries = new ColorEntry[] {
      new ColorEntry(0,  new Color(160,255,163)),
      new ColorEntry(0.25, new Color(255,251,147)),
      new ColorEntry(0.5, new Color(255,202,137)),
      new ColorEntry(0.75, new Color(206,121,41)),
      new ColorEntry(1, Color.WHITE)
  };
  
  
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
    
//    System.out.println("HipsoSampler.getColor: " + r + "," + g + "," + b);
    
    return new Color(VecmathUtil.clamp(r, 0, 255),VecmathUtil.clamp(g, 0, 255),VecmathUtil.clamp(b, 0, 255));
    
  }
  
  private class ColorEntry {
    
    double val;
    Color col;
    
    public ColorEntry(double val, Color col) {
      this.val = val;
      this.col = col;
    }
    
    
    
  }
  
}
