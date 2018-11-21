package gui.widget;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class DoublePanel extends NumPanel {

  private static final long serialVersionUID = 1L;
  
  private double defVal;
  // private NumberFormat formatter = new DecimalFormat("#0.00000");
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

  public void setValue(double roughness) {
    tf.setText(formatter.format(roughness));
  }

}