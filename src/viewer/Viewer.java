package viewer;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gen.TerrainGen;

public class Viewer {

  public static void main(String[] args) {
    new Viewer();
  }
  
//  private int width = 250;
//  private int height = 250;
  
  private TerrainGen tg = new TerrainGen();
  
  private ImagePanel ip;
  private ImagePanel np;
  private ImagePanelBlend blendP;
  
  private IntPanel samplePan;
  private IntPanel octPan;
  private DoublePanel roughPan;
  private DoublePanel scalePan;
  private DoublePanel waterPan;
  
  public Viewer() {
    
    
    ip = new ImagePanel(null);
    np = new ImagePanel(null);
    blendP = new ImagePanelBlend();
    samplePan = new IntPanel("Samps", 4, 250);
    octPan = new IntPanel("Oct", 3, 7);
    roughPan = new DoublePanel("Rgh", 4, 0.5);
    scalePan = new DoublePanel("Scale", 6, 0.005);
    waterPan = new DoublePanel("Water", 3, 0.5);
    JButton updateB = new JButton("Update");
    JButton seedB = new JButton("Seed");
    
    
    updateB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        updateImage();
      }
    });
    
    seedB.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent arg0) {
        Random r = new Random();
        tg.setSeed(r.nextLong());
        updateImage();
      }
    });
    
    
    JPanel southPan = new JPanel(new FlowLayout());
    southPan.add(samplePan);
    southPan.add(octPan);
    southPan.add(roughPan);
    southPan.add(scalePan);
    southPan.add(waterPan);
    southPan.add(seedB);
    southPan.add(updateB);
    
    JPanel imagePan = new JPanel(new GridLayout(1,2));
    imagePan.add(ip);
    imagePan.add(np);
    imagePan.add(blendP);
    
    
    JPanel rootPan = new JPanel(new BorderLayout());
    rootPan.add(imagePan, BorderLayout.CENTER);
    rootPan.add(southPan, BorderLayout.SOUTH);
    
    JFrame f = new JFrame("Noise Height Map");
    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    f.setSize(800, 800);
    f.setContentPane(rootPan);
    f.setVisible(true);

  }
  
  private void updateImage() {
    
    tg.setOctaves(octPan.getVal());
    tg.setRoughness(roughPan.getVal());
    tg.setScale(scalePan.getVal());
    
    float[] hm = tg.generateHeightmap(samplePan.getVal(), samplePan.getVal());
    
//    double[][] hm = { {0,0,0}, {0,1,0}, {0,0,0}};
    
   
    HeightMapRenderer ren = new HeightMapRenderer();
    ren.setWaterHeight(waterPan.getVal());
    BufferedImage colorImage = ren.createImage(hm,samplePan.getVal(), samplePan.getVal());
    ip.setImage(colorImage);
    ip.revalidate();
    ip.repaint();
    
    
    NormalRenderer nren = new NormalRenderer();
    nren.setWaterHeight(waterPan.getVal());
    BufferedImage normalImage = nren.createImage(hm, samplePan.getVal(), samplePan.getVal());
    np.setImage(normalImage );
    np.revalidate();
    np.repaint();
    
//    blendP.setImage(colorImage);
//    blendP.setBlend(normalImage);
//    blendP.setImage(normalImage);
//    blendP.setBlend(colorImage);
    
    blendP.setImage(nren.shadeImage(colorImage, normalImage));
    blendP.setBlend(null);
    blendP.revalidate();
    blendP.repaint();
    
  }

  private class NumPanel extends JPanel {
    
    JTextField tf;
    
    public NumPanel(String label, int size) {
      setLayout(new FlowLayout());
      add(new JLabel(label + ":"));
      tf = new JTextField(size);
      add(tf);
    }
    
  }
  
  private class IntPanel extends NumPanel {

    private int defVal;
    
    public IntPanel(String label, int size, int defVal) {
      super(label, size);
      this.defVal = defVal;
      tf.setText(defVal + "");
    }
    
    public int getVal() {
      try {
        return Integer.parseInt(tf.getText());
      } catch (Exception e) {
        tf.setText(defVal + "");
        return defVal;
      }
    }
    
  }
  
  private class DoublePanel extends NumPanel {

    private double defVal;
    //private NumberFormat formatter = new DecimalFormat("#0.00000");
    private NumberFormat formatter = new DecimalFormat();
    
    public DoublePanel(String label, int size, double defVal) {
      super(label, size);
      this.defVal = defVal;
      tf.setText(formatter.format(defVal));
    }
    
    public double getVal() {
      try {
        return Double.parseDouble(tf.getText());
      } catch (Exception e) {
        tf.setText(formatter.format(defVal));
        return defVal;
      }
    }
    
  }
  
  private class ImagePanel extends JPanel {

    BufferedImage image = null;
    
    public ImagePanel(BufferedImage image) {
      this.image = image;
    }

    public void setImage(BufferedImage image) {
      this.image = image;
    }

    @Override
    public void paint(Graphics g) {
      g.setColor(Color.BLACK);
      g.fillRect(0, 0, getWidth(), getHeight());
//      System.out.println("Crap.ImagePanel.print: " + image);
      if(image == null) {
        return;
      }
      //g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), this);
      g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
    }

    @Override
    public Dimension getPreferredSize() {
      if(image == null) {
        return new Dimension(800, 800);
      }
      return new Dimension(image.getWidth(), image.getHeight());
    }
    
  }
  
  private class ImagePanelBlend extends ImagePanel {

    BufferedImage blend = null;
    
    public ImagePanelBlend() {
      super(null);
    }
    
    public void setBlend(BufferedImage image) {
      blend = image;
    }
    
    @Override
    public void paint(Graphics g) {
      super.paint(g);
      if(blend == null) {
        return;
      }
      Graphics2D g2d= (Graphics2D)g;
      
      //g2d.setComposite(new );
      //g.drawImage(image, 0, 0, image.getWidth(), image.getHeight(), this);
      
          
          AlphaComposite ac =    AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.35f);
          g2d.setComposite(ac);
      g.drawImage(blend, 0, 0, getWidth(), getHeight(), this);
    }
    
    
  }
}
