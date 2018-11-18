package gui.widget;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NumPanel extends JPanel {

  private static final long serialVersionUID = 1L;
  
  JTextField tf;

  public NumPanel(String label, int size) {
    setLayout(new FlowLayout());
    add(new JLabel(label + ":"));
    tf = new JTextField(size);
    add(tf);
  }

}