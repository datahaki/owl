// code by gjoel, jph
package ch.alpine.owl.gui.ren;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Objects;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.lane.LaneInterface;
import ch.alpine.sophus.gui.ren.PathRender;
import ch.alpine.tensor.Tensor;

public class LaneRender implements RenderInterface {
  private final PathRender pathRenderL = new PathRender(new Color(255, 128, 128, 192), 1);
  private final PathRender pathRenderR = new PathRender(new Color(128, 192, 128, 192), 1);

  public void setLane(LaneInterface laneInterface, boolean cyclic) {
    if (Objects.nonNull(laneInterface))
      setBoundaries(laneInterface.leftBoundary(), laneInterface.rightBoundary(), cyclic);
    else
      setBoundaries(null, null, cyclic);
  }

  public void setBoundaries(Tensor leftBoundary, Tensor rightBoundary, boolean cyclic) {
    pathRenderL.setCurve(leftBoundary, cyclic);
    pathRenderR.setCurve(rightBoundary, cyclic);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    pathRenderL.render(geometricLayer, graphics);
    pathRenderR.render(geometricLayer, graphics);
  }
}
