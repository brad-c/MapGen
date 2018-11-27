package worldGen.gen;

public class HeightMapProvider {

  private float[] heightData;
  private int size;
  
  private float[] scaledData;
  private int scaledSize;

  public HeightMapProvider() {
  }

  public float[] getOrUpdateHeightMap(int sizeIn, boolean returnCopy) {
    if (heightData == null) {
      this.size = sizeIn;
      System.out.println("HeightMapProvider.getOrUpdateHeightMap: No data loaded");
      int s2 = size*size;
      heightData = new float[s2];
      for(int i=0;i<s2; i++) {
        heightData[i] = 0.5f;
      }
    }
    
    float[] result;
    if(sizeIn == size) {
      result = heightData;
    } else if(scaledData != null && scaledSize == sizeIn) {
      result = scaledData;
    } else {
      scaledData = HeightMapUtil.scale(heightData, size, sizeIn);
      scaledSize = sizeIn;
      result = scaledData;
    }
   
    if (returnCopy) {
      float[] copy = new float[result.length];
      System.arraycopy(result, 0, copy, 0, result.length);
      result = copy;
    }

    return result;
  }

  public float[] getHeightData(boolean copyData) {
    float[] result = heightData;
    if(copyData) {
      float[] copy = new float[result.length];
      System.arraycopy(result, 0, copy, 0, result.length);
      result = copy;
    }
    return result;
  }

  public void setHeightData(float[] heightData) {
    this.heightData = heightData;
    if(heightData == null) {
      size = -1;
    } else {
      size = (int)Math.sqrt(heightData.length);
    }
    scaledData = null;
  }
  
  public int getSize() {
    return size;
  }

  public void setSource(String baseHeightMapSource) {
    // TODO Auto-generated method stub
    
  }

  public String getSource() {
    // TODO Auto-generated method stub
    return null;
  }

}
