// code by jph
package ch.alpine.owl.gui.win;

import ch.alpine.owl.glc.core.TrajectoryPlanner;

public enum OwlyGui {
  ;
  public static OwlyFrame start() {
    OwlyFrame owlyFrame = new OwlyFrame();
    owlyFrame.jFrame.setVisible(true);
    return owlyFrame;
  }

  public static OwlyFrame glc(TrajectoryPlanner trajectoryPlanner) {
    OwlyFrame owlyFrame = new OwlyFrame();
    owlyFrame.setGlc(trajectoryPlanner);
    owlyFrame.jFrame.setVisible(true);
    return owlyFrame;
  }
}
