// code by jph
package ch.alpine.owl.img;

import java.awt.Window;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.alpine.bridge.pro.WindowProvider;

public class StringAreaShow implements WindowProvider {
  String string;

  public StringAreaShow(String string) {
    this.string = string;
  }

  @Override
  public Window getWindow() {
    JFrame jFrame = new JFrame();
    JTextArea jTextArea = new JTextArea(string);
    JScrollPane jScrollPane = new JScrollPane(jTextArea);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jScrollPane);
    return jFrame;
  }

  static void main() {
    new StringAreaShow("here").runStandalone();
  }
}
