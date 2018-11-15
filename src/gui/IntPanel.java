package gui;

public class IntPanel extends NumPanel {

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