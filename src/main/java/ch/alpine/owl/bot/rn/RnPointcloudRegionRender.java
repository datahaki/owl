// code by jph
package ch.alpine.owl.bot.rn;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.r2.CirclePoints;

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
    for (Tensor point : points) {
      geometricLayer.pushMatrix(Se2Matrix.translation(point));
      Path2D path2D = geometricLayer.toPath2D(polygon, true);
      graphics.setColor(RegionRenders.COLOR);
      graphics.fill(path2D);
      graphics.setColor(RegionRenders.BOUNDARY);
      graphics.draw(path2D);
      geometricLayer.popMatrix();
    }
  }
}
