// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.owl.glc.core.TrajectoryPlanner;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.win.GeometricLayer;

// LONGTERM decide on purpose of HudRender, for now, do nothing
class HudRender implements RenderInterface {
  private static final Color SHADING = new Color(0, 0, 0, 64);
  // ---
  @SuppressWarnings("unused")
  private final TrajectoryPlanner trajectoryPlanner;

  HudRender(TrajectoryPlanner trajectoryPlanner) {
    this.trajectoryPlanner = trajectoryPlanner;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(SHADING);
  }
}
