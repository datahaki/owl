// code by jph
package ch.alpine.owl.sea;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

import ch.alpine.bridge.awt.ScreenRectangles;
import ch.alpine.bridge.ref.util.PanelFieldsEditor;
import ch.alpine.bridge.swing.LookAndFeels;

public class BoatEditor implements Runnable {
  private final JFrame jFrame = new JFrame();
  private final BoatObject boatObject;
  private final JTextArea jTextArea = new JTextArea();

  public BoatEditor(Boat boat) {
    jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    boatObject = new BoatObject(boat);
    JPanel jPanel = new JPanel(new BorderLayout());
    {
      PanelFieldsEditor pfe = PanelFieldsEditor.nested(boatObject);
      // pfe.updateJComponents();
      jPanel.add(pfe.getJPanel(), BorderLayout.NORTH);
      pfe.addUniversalListener(this);
    }
    jPanel.add(jTextArea, BorderLayout.CENTER);
    run();
    jFrame.setContentPane(jPanel);
    jFrame.setBounds(100, 100, 600, 500);
    ScreenRectangles.create().placement(jFrame);
  }

  @Override
  public void run() {
    boatObject.lwl = boatObject.loa;
    jTextArea.setText(boatObject.create().textValues());
  }

  static void main() {
    LookAndFeels.autoDetect();
    BoatEditor boatEditor = new BoatEditor(MyBoat.MONSUN_31);
    boatEditor.jFrame.setVisible(true);
  }
}
