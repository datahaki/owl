// code by jph
package ch.alpine.owl.img;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.awt.WindowBounds;

public class StringAreaShow {
  private final JFrame jFrame = new JFrame();

  public StringAreaShow(String string) {
    WindowBounds.persistent(jFrame, AbstractDemo.WINDOW.properties(StringAreaShow.class));
    // ---
    JTextArea jTextArea = new JTextArea(string);
    JScrollPane jScrollPane = new JScrollPane(jTextArea);
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    jFrame.setContentPane(jScrollPane);
    jFrame.setVisible(true);
  }
}
