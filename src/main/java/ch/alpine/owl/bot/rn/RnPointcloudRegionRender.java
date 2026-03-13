// code by jph
package ch.alpine.owl.bot.rn;

import java.awt.Graphics2D;

import ch.alpine.ascony.ren.ColorPair;
import ch.alpine.ascony.ren.PointsRender;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.rot.CirclePoints;

public class RnPointcloudRegionRender implements RenderInterface {
  private static final int RESOLUTION = 16;
  // ---
  private final Tensor points;
  private final Tensor polygon;

  public RnPointcloudRegionRender(RnPointcloudRegion rnPointcloudRegion) {
    points = rnPointcloudRegion.points();
    Scalar radius = rnPointcloudRegion.radius();
    polygon = CirclePoints.of(RESOLUTION).multiply(radius);
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    new PointsRender(ColorPair.REGION, Se2Matrix::translation, polygon, points) //
        .render(geometricLayer, graphics);
  }
}
