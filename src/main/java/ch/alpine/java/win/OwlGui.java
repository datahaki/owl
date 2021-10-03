// code by jph
package ch.alpine.java.win;

import ch.alpine.owl.glc.core.TrajectoryPlanner;

public enum OwlGui {
  ;
  public static OwlFrame start() {
    OwlFrame owlyFrame = new OwlFrame();
    owlyFrame.jFrame.setVisible(true);
    return owlyFrame;
  }

  public static OwlFrame glc(TrajectoryPlanner trajectoryPlanner) {
    OwlFrame owlyFrame = new OwlFrame();
    owlyFrame.setGlc(trajectoryPlanner);
    owlyFrame.jFrame.setVisible(true);
    return owlyFrame;
  }
}
