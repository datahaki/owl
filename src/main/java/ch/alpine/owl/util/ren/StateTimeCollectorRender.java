// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeCollector;

public class StateTimeCollectorRender implements RenderInterface {
  private static final Color COLOR = new Color(0, 0, 0, 128);
  private static final int SIZE = 2;
  // ---
  private final StateTimeCollector stateTimeCollector;

  public StateTimeCollectorRender(StateTimeCollector stateTimeCollector) {
    this.stateTimeCollector = stateTimeCollector;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    graphics.setColor(COLOR);
    try {
      for (StateTime stateTime : stateTimeCollector.getMembers()) {
        Point2D point2d = geometricLayer.toPoint2D(stateTime.state());
        graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), SIZE, SIZE);
      }
    } catch (Exception exception) {
      // concurrent modification
    }
  }
}
