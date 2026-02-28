// code by jph
package ch.alpine.owl.util.win;

import ch.alpine.owlets.glc.core.TrajectoryPlanner;

public enum OwlGui {
  ;
  public static OwlFrame start() {
    OwlFrame owlFrame = new OwlFrame();
    owlFrame.jFrame.setVisible(true);
    return owlFrame;
  }

  public static OwlFrame glc(TrajectoryPlanner trajectoryPlanner) {
    OwlFrame owlFrame = new OwlFrame();
    owlFrame.setGlc(trajectoryPlanner);
    owlFrame.jFrame.setBounds(100, 100, 800, 800);
    owlFrame.jFrame.setVisible(true);
    return owlFrame;
  }
}
