package gui.ramp;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;

import gen.ElevationRamp;
import render.WorldRenderer;

public class TerrainElevationRampEditor {

  public static void showTestFrame() {
    TerrainElevationRampEditor ed = new TerrainElevationRampEditor();
    JFrame f = new JFrame("Elevation Offsets");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.getContentPane().add(ed.getEditor());
    f.setSize(380, 520);
    f.setVisible(true);
  }

  public static void showEditorFrame(JFrame owner, WorldRenderer rend) {
    TerrainElevationRampEditor ed = new TerrainElevationRampEditor();
    ed.setWorldRenderer(rend);
   

    JDialog d = new JDialog(owner, "Elevation Offsets", false);
    d.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    d.getContentPane().add(ed.getEditor());
    d.setSize(380, 520);
    if (owner != null) {
      d.setLocation(Math.max(0, owner.getLocation().x - (int)d.getSize().getWidth()), owner.getLocation().y);

    }
    d.setVisible(true);
  }

  

  private ElevationRampEditor landEd;
  private ElevationRampEditor waterEd;

  private JPanel rootPan;

  private WorldRenderer rend;
  
  public TerrainElevationRampEditor() {
    landEd = new ElevationRampEditor(true);
    waterEd = new ElevationRampEditor(false);

    landEd.getTabbedPane().setBorder(null);

    JPanel pan = new JPanel();
    GridLayout gl = new GridLayout(2, 2);
    gl.setHgap(0);
    gl.setVgap(0);
    pan.setLayout(gl);

    landEd.setGraphAlignment(1, 1);
    landEd.setGraphBackgroun(Color.WHITE);

    waterEd.setGraphAlignment(0, 0);
    waterEd.setGraphBackgroun(Color.WHITE);

    JButton updateB = new JButton("Update");
    updateB.addActionListener(new ActionListener() {
      
      @Override
      public void actionPerformed(ActionEvent e) {
        if(rend != null) {
          rend.enqueue(new Runnable() {
            @Override
            public void run() {
              rend.updateTerrain();
            }
          });
        }
        
      }
    });
    
    JPanel blPan = createFiller();
    blPan.setLayout(new GridBagLayout());
    blPan.add(createFiller(), new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    blPan.add(updateB, new GridBagConstraints(1, 1, 1, 1, 0, 0, GridBagConstraints.SOUTHEAST, GridBagConstraints.NONE, new Insets(6, 6, 6, 6), 0, 0));
    
    pan.add(createFiller());
    pan.add(landEd.getEditor());
    pan.add(waterEd.getEditor());
    pan.add(blPan);

    rootPan = new JPanel(new BorderLayout());
    rootPan.add(pan, BorderLayout.CENTER);
  }
  
  public void setWorldRenderer(WorldRenderer rend) {
    this.rend = rend;
    if(rend == null) {
      return;
    }
    landEd.addElevationRampListener(new ElevationRampListener() {
      @Override
      public void elevationRampChanged(ElevationRamp newRamp) {
        rend.enqueue(new Runnable() {
          @Override
          public void run() {
            rend.getTerrainGenerator().setLandElevationRamp(newRamp);
          }
        });

      }
    });
    landEd.setElevationRamp(rend.getTerrainGenerator().getLandElevationRamp());

    waterEd.addElevationRampListener(new ElevationRampListener() {
      @Override
      public void elevationRampChanged(ElevationRamp newRamp) {
        rend.enqueue(new Runnable() {
          @Override
          public void run() {
            rend.getTerrainGenerator().setWaterElevationRamp(newRamp);
          }
        });

      }
    });
    waterEd.setElevationRamp(rend.getTerrainGenerator().getWaterElevationRamp());
  }

  private JPanel createFiller() {
    JPanel fillerPan = new JPanel();
    fillerPan.setBackground(Color.white);
    return fillerPan;
  }

  public Component getEditor() {
    return rootPan;
  }

}
