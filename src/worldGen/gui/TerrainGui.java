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
import java.net.URL;
import java.util.concurrent.Callable;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import com.jme3.export.xml.XMLExporter;
import com.jme3.export.xml.XMLImporter;
import com.jme3.system.AppSettings;
import com.jme3.system.JmeCanvasContext;

import worldGen.gui.heightmap.EditorPanel;
import worldGen.render.WorldRenderer;
import worldGen.render.WorldRenderer.ViewType;
import worldGen.state.SaveLoadContext;
import worldGen.state.WorldGenProject;
import worldGen.util.FileUtil;

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

        try {
          for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
            if ("Nimbus".equals(info.getName())) {
              UIManager.setLookAndFeel(info.getClassName());
              break;
            }
          }
        } catch (Exception e) {
        }

        JPopupMenu.setDefaultLightWeightPopupEnabled(false);
        terrainGui.createGui();
        terrainGui.startApp();
        terrainGui.show();
      }

    });
  }

  public static final String WGX_FILE_EXT = "wgx";

  public static enum GuiMode {
    TERRAIN_EDITOR,
    HEIGHTMAP_EDITOR
  }

  private WorldRenderer app;
  private Canvas canvas;
  private JFrame frame;

  private TerrainParamatersPanel terrainPan;

  private TerrainVisualsPanel terrainVisualsPan;

  private EditorPanel heightMapPan;

  // View
  private JToggleButton view3dB;
  private JToggleButton view2dB;

//  private GuiMode mode = GuiMode.TERRAIN_EDITOR;
//  private WorldRendererState terrGenState;
//  private WorldRendererState hmEdState;

  private JTabbedPane editorTP;

  private JButton saveB;
  private JButton saveAsB;
  private JButton loadB;
  private JButton newB;

  private File currentSaveFile;

  // layout
  private JPanel rootPan;
  private JPanel canvasPan;

  public TerrainGui() {
  }

  public WorldRenderer getWorldRenderer() {
    return app;
  }

  public JFrame getFrame() {
    return frame;
  }
  
  public Canvas getCanvas() {
    return canvas;
  }
  
  public JTabbedPane getTabbedPane() {
    return editorTP;
  }

  public void updateGUI(WorldRenderer ren) {
    this.app = ren;
    terrainVisualsPan.updateGUI(ren);
    terrainPan.updateGUI(ren);
    heightMapPan.updateGUI(ren);
    view2dB.setSelected(ren.getViewType() == ViewType.TWO_D);
    view3dB.setSelected(ren.getViewType() == ViewType.THREE_D);
  }

  private void createGui() {
    initComponenets();
    addComponents();
    addListeners();
    frame.getContentPane().add(rootPan);
    frame.pack();
  }

  private void addComponents() {
    JPanel viewPan = new JPanel(new FlowLayout(FlowLayout.LEFT));
    viewPan.add(view3dB);
    viewPan.add(view2dB);

    JPanel savePan = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    savePan.add(saveB);
    savePan.add(saveAsB);
    savePan.add(loadB);
    savePan.add(newB);

    JPanel northPan = new JPanel(new BorderLayout());
    northPan.add(viewPan, BorderLayout.WEST);
    northPan.add(savePan, BorderLayout.EAST);

    canvasPan.setLayout(new BorderLayout());
    canvasPan.add(northPan, BorderLayout.NORTH);
    canvasPan.add(canvas, BorderLayout.CENTER);

    rootPan = new JPanel(new BorderLayout());
    rootPan.add(canvasPan, BorderLayout.CENTER);
    rootPan.add(editorTP, BorderLayout.WEST);
  }

  private void initComponenets() {

    canvasPan = new JPanel();

    terrainPan = new TerrainParamatersPanel(this);
    terrainVisualsPan = new TerrainVisualsPanel(this);
    heightMapPan = new EditorPanel(this);

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
    saveAsB = new JButton("Save As");

    loadB = new JButton("Load");
    newB = new JButton("New");

    // Frame
    frame = new JFrame("World Creator");
    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    try {
      URL url = Thread.currentThread().getContextClassLoader().getResource("icons/globe.png");
      ImageIcon img = new ImageIcon(url);
      frame.setIconImage(img.getImage());
    } catch (Exception e) {
      System.out.println("TerrainGui.initComponenets: Could not load frame icon");
    }

  }

  private void addListeners() {

    saveB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        doSave();
      }

    });

    saveAsB.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        doSaveAs();
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

    // load default project
    WorldGenProject project = new WorldGenProject();
    project.apply(app);

    terrainPan.updateTerrain();
    frame.setLocationRelativeTo(null);
    frame.setVisible(true);
  }

  private void doSaveAs() {
    JFileChooser fc = new JFileChooser();
    if (currentSaveFile != null) {
      fc.setCurrentDirectory(currentSaveFile.getParentFile());
    } else {
      fc.setCurrentDirectory(new File("."));
    }
    fc.addChoosableFileFilter(new WGFileFilter());
    fc.setAcceptAllFileFilterUsed(false);

    int val = fc.showSaveDialog(frame);
    if (val == JFileChooser.APPROVE_OPTION) {
      File sel = fc.getSelectedFile();
      if (!sel.getName().endsWith(".wgx")) {
        sel = new File(sel.getParentFile(), sel.getName() + ".wgx");
      }
      saveProject(sel);
    }

  }

  private void doSave() {
    if (currentSaveFile == null) {
      doSaveAs();
    } else {
      saveProject(currentSaveFile);
    }
  }

  private void saveProject(File file) {
    
    setSaveLoadContent(file);
    notifyOfSave();
    
    WorldGenProject project = new WorldGenProject(app);
    // BinaryExporter exporter = new BinaryExporter();
    XMLExporter exporter = new XMLExporter();
    try {
      exporter.save(project, file);
      currentSaveFile = file;
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

 
  private void notifyOfSave() {
    terrainVisualsPan.onSave();
    terrainPan.onSave();
    heightMapPan.onSave();
  }
  

  private void doLoad() {
    JFileChooser fc = new JFileChooser();
    if (currentSaveFile != null) {
      fc.setCurrentDirectory(currentSaveFile.getParentFile());
    } else {
      fc.setCurrentDirectory(new File("."));
    }
    fc.addChoosableFileFilter(new WGFileFilter());
    fc.setAcceptAllFileFilterUsed(false);

    // new WGFileFilter()
    int val = fc.showOpenDialog(frame);
    if (val == JFileChooser.APPROVE_OPTION) {
      File sel = fc.getSelectedFile();
      loadProject(sel);
    }
  }

  private void loadProject(File file) {

    setSaveLoadContent(file);
    
    WorldGenProject project = null;
    // BinaryImporter importer = new BinaryImporter();
    XMLImporter importer = new XMLImporter();
    try {
      project = (WorldGenProject) importer.load(file);
      currentSaveFile = file;
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    if (project == null) {
      return;
    }

    applyProject(project, false);

  }

  private void applyProject(final WorldGenProject p, boolean resetCamera) {
    app.getHeightMapEditor().clearChanges();
    app.enqueue(new Runnable() {

      @Override
      public void run() {
        // apply setting to the internals
        p.apply(app);
        app.updateTerrain();
        if(resetCamera) {
          app.resetCameraToDefault();
        }
        SwingUtilities.invokeLater(new Runnable() {
          @Override
          public void run() {
            // then update the GUI
            updateGUI(app);
          }
        });
        

      }
    });
  }

  private void doNew() {
    applyProject(new WorldGenProject(), true);
  }
  
  private void setSaveLoadContent(File file) {
    SaveLoadContext ctx = SaveLoadContext.INSTANCE;
    ctx.setCurrentFile(file);
    ctx.setSaveBinaryDataExternally(true);
    ctx.setProjectName(FileUtil.getFileNameNoExtension(file));
  }

  private class WGFileFilter extends javax.swing.filechooser.FileFilter {

    @Override
    public boolean accept(File f) {
      if (f.isDirectory()) {
        return true;
      }

      String extension = FileUtil.getExtension(f);
      return WGX_FILE_EXT.equals(extension);
    }

    @Override
    public String getDescription() {
      return "World Gen Projects (.wgx)";
    }
  }

  

}
