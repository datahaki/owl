// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.owl.glc.core.TrajectoryPlanner;

// TODO decide on purpose of HudRender, for now, do nothing
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
