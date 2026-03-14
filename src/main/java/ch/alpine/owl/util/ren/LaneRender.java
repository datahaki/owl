// code by gjoel, jph
package ch.alpine.owl.util.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.ascony.ren.PathRender;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.lane.LaneInterface;
import ch.alpine.tensor.Tensor;

public class LaneRender implements RenderInterface {
  public void setLane(LaneInterface laneInterface, boolean cyclic) {
    if (Objects.nonNull(laneInterface))
      setBoundaries(laneInterface.leftBoundary(), laneInterface.rightBoundary(), cyclic);
    else
      setBoundaries(null, null, cyclic);
  }

  Tensor leftBoundary;
  Tensor rightBoundary;
  boolean cyclic;

  public void setBoundaries(Tensor leftBoundary, Tensor rightBoundary, boolean cyclic) {
    this.leftBoundary = leftBoundary;
    this.rightBoundary = rightBoundary;
    this.cyclic = cyclic;
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    new PathRender(new Color(255, 128, 128, 192), 1, leftBoundary, cyclic).render(geometricLayer, graphics);
    new PathRender(new Color(128, 192, 128, 192), 1, rightBoundary, cyclic).render(geometricLayer, graphics);
  }
}
