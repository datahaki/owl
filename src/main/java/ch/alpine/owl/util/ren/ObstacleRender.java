// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import ch.alpine.ascona.util.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.StateTimeCollector;

/** coordinates of detected obstacles are rendered as gray squares */
public class ObstacleRender implements RenderInterface {
  private static final Color COLOR = new Color(0, 0, 0, 128);
  private static final int SIZE = 2;
  // ---
  private Collection<StateTime> collection;

  public ObstacleRender(Collection<StateTime> collection) {
    this.collection = collection;
  }

  @Override
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    if (Objects.isNull(collection))
      return;
    // ---
    graphics.setColor(COLOR);
    for (StateTime stateTime : collection) {
      Point2D point2d = geometricLayer.toPoint2D(stateTime.state());
      graphics.fillRect((int) point2d.getX(), (int) point2d.getY(), SIZE, SIZE);
    }
  }

  public void fromStateTimeCollector(Object object) {
    collection = object instanceof StateTimeCollector stateTimeCollector //
        ? new HashSet<>(stateTimeCollector.getMembers()) //
        : Collections.emptySet();
  }
}
