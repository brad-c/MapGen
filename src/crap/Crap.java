package crap;

import java.io.File;
import java.io.IOException;

import com.jme3.export.xml.XMLExporter;
import com.jme3.renderer.Camera;

import worldGen.gen.SimplexNoiseGen;
import worldGen.state.TerrainGenerationParameters;
import worldGen.state.WorldGenProject;

public class Crap {

  public static void main(String[] args) {
   // new CurveTest();
//    SplineRampEditor.showTestFrame();
//    ExponentialRampEditor.showTestFrame();
//    ElevationRampEditor.showTestFrame();
//    TerrainElevationRampEditor.showTestFrame();
    WorldGenProject proj = new WorldGenProject();
    
    TerrainGenerationParameters tp = new TerrainGenerationParameters();
    tp.setErodeFilter(1);
    
    SimplexNoiseGen ng = new SimplexNoiseGen();
    ng.setSampleSpacing(1000);
    
    tp.setNoiseGen(ng);
    
    Camera cam = new Camera(123,456);
    
    XMLExporter x = new XMLExporter();
    
    try {
      x.save(proj, new File("D:\\Dev\\temp\\proj.xml"));
//      x.save(tp, new File("D:\\Dev\\temp\\tp.xml"));
//      x.save(cam, new File("D:\\Dev\\temp\\cam.xml"));
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
