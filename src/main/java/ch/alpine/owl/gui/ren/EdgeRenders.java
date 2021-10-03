// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;

import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.glc.rl2.RelaxedDebugUtils;
import ch.alpine.owl.glc.rl2.RelaxedTrajectoryPlanner;

public enum EdgeRenders {
  ;
  private static final int LIMIT = 2000;
  private static final Color COLOR = new Color(0, 0, 255, 128);

  public static RenderInterface of(RelaxedTrajectoryPlanner relaxedTrajectoryPlanner) {
    EdgeRender edgeRender = new EdgeRender(LIMIT, COLOR);
    edgeRender.setCollection(RelaxedDebugUtils.allNodes(relaxedTrajectoryPlanner));
    return edgeRender.getRender();
  }
}
