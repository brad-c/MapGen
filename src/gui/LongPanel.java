package gui;

public class LongPanel extends NumPanel {

  private long defVal;

  public LongPanel(String label, int size, long defVal) {
    super(label, size);
    this.defVal = defVal;
    tf.setText(defVal + "");
  }

  public long getVal() {
    try {
      return Long.parseLong(tf.getText());
    } catch (Exception e) {
      tf.setText(defVal + "");
      return defVal;
    }
  }
}
