package worldGen.gui;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Callable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

import com.jme3.export.xml.XMLExporter;
import com.jme3.export.xml.XMLImporter;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import worldGen.render.WorldRenderer;
import worldGen.render.WorldRenderer.ViewType;
import worldGen.state.WorldGenProject;

public class TerrainGui {

  public static void main(String[] args) {

    TerrainGui terrainGui = new TerrainGui();
    terrainGui.createApp();
    try {
      Thread.sleep(500);
    } catch (InterruptedException ex) {
    }
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {
        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        terrainGui.createGui();
        terrainGui.startApp();
        terrainGui.show();
      }

    });
  }

  private WorldRenderer app;
  private Canvas canvas;
  private JFrame frame;

  private TerrainParamatersPanel terrainPan;

  private TerrainVisualsPanel terrainVisualsPan;

  // View
  private JToggleButton view3dB;
  private JToggleButton view2dB;

  private JTabbedPane editorTP;

  private JButton saveB;
  private JButton loadB;
  private JButton newB;

  // layout
  private JPanel rootPan;

  public TerrainGui() {
  }

  public WorldRenderer getWorldRenderer() {
    return app;
  }

  public JFrame getFrame() {
    return frame;
  }

  private void createGui() {
    initComponenets();
    addComponents();
    addListeners();
    frame.getContentPane().add(rootPan);
    frame.pack();
  }

  private void addComponents() {
    JPanel northPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    northPan.add(view3dB);
    northPan.add(view2dB);
    northPan.add(saveB);
    northPan.add(loadB);
    northPan.add(newB);

    JPanel canvasPan = new JPanel(new BorderLayout());
    canvasPan.add(northPan, BorderLayout.NORTH);
    canvasPan.add(canvas, BorderLayout.CENTER);

    rootPan = new JPanel(new BorderLayout());
    rootPan.add(canvasPan, BorderLayout.CENTER);
    rootPan.add(editorTP, BorderLayout.WEST);
  }

  private void initComponenets() {
    terrainPan = new TerrainParamatersPanel(this);
    terrainVisualsPan = new TerrainVisualsPanel(this);

    editorTP = new JTabbedPane();
    editorTP.add("Generator", terrainPan);
    editorTP.add("Renderer", terrainVisualsPan);

    // Camera
    view3dB = new JToggleButton("3D");
    view2dB = new JToggleButton("2D");
    boolean is3d = app.getViewType() == ViewType.THREE_D;
    view3dB.setSelected(is3d);
    view2dB.setSelected(!is3d);

    ButtonGroup bg = new ButtonGroup();
    bg.add(view2dB);
    bg.add(view3dB);

    saveB = new JButton("Save");
    loadB = new JButton("Load");
    newB = new JButton("New");

    // Frame
    frame = new JFrame("World Creator");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }
  
  public void updateGUI(WorldRenderer ren) {
    this.app = ren;
    terrainVisualsPan.updateGUI(ren);
    terrainPan.updateGUI(ren);
    view2dB.setSelected(ren.getViewType() == ViewType.TWO_D);
    view3dB.setSelected(ren.getViewType() == ViewType.THREE_D);
  }

  private void addListeners() {

    saveB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        doSave();
      }

    });

    loadB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        doLoad();
      }

    });
    
    newB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        doNew();
      }

    });

    frame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        app.stop();
      }
    });

    canvas.addComponentListener(new ComponentAdapter() {

      @Override
      public void componentResized(ComponentEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.canvasResized(canvas);
          }
        });
      }

    });

    view2dB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.setViewType(ViewType.TWO_D);
          }
        });

      }
    });

    view3dB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        app.enqueue(new Runnable() {
          @Override
          public void run() {
            app.setViewType(ViewType.THREE_D);
          }
        });

      }
    });

  }

  public WorldRenderer createApp() {
    AppSettings settings = new AppSettings(true);
    settings.setWidth(640);
    settings.setHeight(480);

    app = new WorldRenderer();

    app.setPauseOnLostFocus(false);
    app.setSettings(settings);
    app.createCanvas();
    app.startCanvas();

    JmeCanvasContext context = (JmeCanvasContext) app.getContext();
    canvas = context.getCanvas();
    canvas.setSize(settings.getWidth(), settings.getHeight());

    return app;
  }

  private void startApp() {
    app.startCanvas();
    app.enqueue(new Callable<Void>() {
      @Override
      public Void call() {
        app.getFlyByCamera().setDragToRotate(true);
        return null;
      }
    });
  }

  private void show() {
    
    //load default project
    WorldGenProject project = new WorldGenProject();
    project.apply(app);
    
    terrainPan.updateTerrain();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }
  

  private void doSave() {
    WorldGenProject project = new WorldGenProject(app);
//    BinaryExporter exporter = new BinaryExporter();
    XMLExporter exporter = new XMLExporter();
    try {
      exporter.save(project, new File("D:\\Dev\\temp\\testSave.wgen"));
      System.out.println("TerrainGui.doSave: " + project);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }

  private void doLoad() {
    WorldGenProject project = null;
    //BinaryImporter importer = new BinaryImporter();
    XMLImporter importer = new XMLImporter();
    try {
      project  = (WorldGenProject)importer.load(new File("D:\\Dev\\temp\\testSave.wgen"));
      System.out.println("TerrainGui.doLoad: " + project);
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    if(project == null) {
      System.out.println("TerrainGui.doLoad: null project");
      return;
    }
   
    final WorldGenProject p = project;
    
    app.enqueue(new Runnable() {
      
      @Override
      public void run() {
      //apply setting to the internals
        p.apply(app);
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            //then update the GUI
            updateGUI(app);
          }
        });
        app.updateTerrain();
        
      }
    });
    
  }
  
  private void doNew() {
    
    
    app.enqueue(new Runnable() {
      @Override
      public void run() {
        final WorldGenProject p = new WorldGenProject();
        //apply setting to the internals
        p.apply(app);
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            //then update the GUI
            updateGUI(app);
          }
        });
        app.updateTerrain();
        app.resetCameraToDefault();
      }
    });
  }

}