package worldGen.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

  public static String getExtension(File f) {
    String ext = null;
    String s = f.getName();
    int i = s.lastIndexOf('.');

    if (i > 0 && i < s.length() - 1) {
      ext = s.substring(i + 1).toLowerCase();
    }
    return ext;
  }
  
  public static String getFileNameNoExtension(File f) {
    String res = f.getName();
    res = res.substring(0, res.lastIndexOf('.'));
    return res;
  }
  
  public static boolean saveHeightMap(File toFile, float[] data) {
    try {
      BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(toFile));
      DataOutputStream das = new DataOutputStream(bos);
      das.writeInt(data.length);
      for(int i=0;i<data.length;i++) {
        das.writeFloat(data[i]);
      }
      bos.flush();
      bos.close();
      return true;
    } catch (IOException e) {
      e.printStackTrace();
      return false;
    }
  }

  public static float[] loadHeightMap(File heightMapFile) {
    try {
      BufferedInputStream bis = new BufferedInputStream(new FileInputStream(heightMapFile));
      DataInputStream dis = new DataInputStream(bis);
      int len = dis.readInt();
      float[] res = new float[len];
      for(int i=0;i<len;i++) {
        res[i] = dis.readFloat();
      }
      dis.close();
      return res;
    } catch(IOException e) {
      e.printStackTrace();
      return null;
    }
  }
  
}
