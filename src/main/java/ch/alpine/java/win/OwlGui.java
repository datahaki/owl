// code by jph
package ch.alpine.java.win;

import ch.alpine.owl.glc.core.TrajectoryPlanner;

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
    owlFrame.jFrame.setVisible(true);
    return owlFrame;
  }
}
