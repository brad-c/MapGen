package gui;

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class NumPanel extends JPanel {

  JTextField tf;

  public NumPanel(String label, int size) {
    setLayout(new FlowLayout());
    add(new JLabel(label + ":"));
    tf = new JTextField(size);
    add(tf);
  }

}