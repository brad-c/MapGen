package worldGen.gen;

import crap.ImageHeightmapLoader;

public class HeightMapProvider {

  float[] heightData;
  int size;

  public HeightMapProvider() {
  }

  public float[] getOrUpdateHeightMap(int size, boolean returnCopy) {
    System.out.println("HeightMapProvider.getOrUpdateHeightMap: ");
    if (heightData == null) {
      System.out.println("HeightMapProvider.getOrUpdateHeightMap: LOADED!!!");
      this.size = size;
      heightData = ImageHeightmapLoader.loadGrayScaleData("textures/circleGradLarge.png", size, 1, true);
    }

    // TODO: Scale
    float[] result = heightData;
    if (returnCopy) {
      result = new float[heightData.length];
      System.arraycopy(heightData, 0, result, 0, heightData.length);
    }

    return result;
  }

  public float[] getHeightData() {
    return heightData;
  }

  public void setHeightData(float[] heightData) {
    System.out.println("HeightMapProvider.setHeightData: ");
    this.heightData = heightData;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

}
