// code by jph
package ch.alpine.owl.gui.ren;

import java.awt.Graphics2D;
import java.awt.geom.Path2D;

import ch.alpine.java.gfx.GeometricLayer;
import ch.alpine.java.ren.RenderInterface;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.sophus.crv.d2.EllipsePoints;
import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/** visualize planar ellipse */
public class EllipseRegionRender implements RenderInterface {
  /** @param ellipsoidRegion
   * @return */
  public static RenderInterface of(EllipsoidRegion ellipsoidRegion) {
    Tensor radius = ellipsoidRegion.radius();
    return new EllipseRegionRender( //
        Extract2D.FUNCTION.apply(ellipsoidRegion.center()), //
        radius.Get(0), radius.Get(1));
  }

  /** @param ballRegion
   * @return */
  public static RenderInterface of(BallRegion ballRegion) {
    return new EllipseRegionRender( //
        Extract2D.FUNCTION.apply(ballRegion.center()), //
        ballRegion.radius(), ballRegion.radius());
  }

  // ---
  private static final int RESOLUTION = 22;
  // ---
  private final Tensor polygon;

  /** @param center vector of length 2
   * @param radiusX
   * @param radiusY */
  private EllipseRegionRender(Tensor center, Scalar radiusX, Scalar radiusY) {
    polygon = Tensor.of(EllipsePoints.of(RESOLUTION, radiusX, radiusY).stream().map(center::add));
  }

  @Override // from RenderInterface
  public void render(GeometricLayer geometricLayer, Graphics2D graphics) {
    Path2D path2D = geometricLayer.toPath2D(polygon);
    path2D.closePath();
    graphics.setColor(RegionRenders.COLOR);
    graphics.fill(path2D);
    graphics.setColor(RegionRenders.BOUNDARY);
    graphics.draw(path2D);
  }
}
