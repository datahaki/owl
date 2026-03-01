// code by jph
package ch.alpine.owl.util.ren;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.function.Predicate;

import ch.alpine.ascony.reg.BallRegionRender;
import ch.alpine.ascony.reg.BufferedImageRegion;
import ch.alpine.ascony.reg.ConeRegionRender;
import ch.alpine.ascony.reg.PolygonRegionRender;
import ch.alpine.ascony.reg.RegionRenders;
import ch.alpine.ascony.ren.ImageRender;
import ch.alpine.ascony.ren.RenderInterface;
import ch.alpine.bridge.gfx.GeometricLayer;
import ch.alpine.owl.bot.rn.RnPointcloudRegion;
import ch.alpine.owl.bot.rn.RnPointcloudRegionRender;
import ch.alpine.owlets.math.state.StateTimeCollector;
import ch.alpine.owlets.math.state.TrajectoryRegionQuery;
import ch.alpine.sophis.crv.d2.alg.PolygonRegion;
import ch.alpine.sophis.reg.BallRegion;
import ch.alpine.sophis.reg.ConeRegion;
import ch.alpine.sophis.reg.EllipsoidRegion;
import ch.alpine.sophis.reg.ImageRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.mat.MatrixQ;

// TODO OWL API implement this as an interface
public enum RegionRenderFactory {
  ;
  // ---
  private static final Scalar TFF = RealScalar.of(255);
  private static final Scalar OBS = RealScalar.of(RegionRenders.RGB);

  static Scalar color(Scalar scalar) {
    return Scalars.isZero(scalar) ? TFF : OBS;
  }

  /** @param image with rank 2
   * @return */
  public static BufferedImage image(Tensor image) {
    return ImageFormat.of(MatrixQ.require(image).maps(RegionRenderFactory::color));
  }

  /** @param region
   * @return new instance of {@link RenderInterface} that visualizes given region,
   * or null if drawing capability is not available for the region */
  public static RenderInterface create(Predicate<Tensor> region) {
    if (region instanceof ImageRegion imageRegion)
      return RegionRenderFactory.createImageRegionRender(imageRegion);
    if (region instanceof BufferedImageRegion bufferedImageRegion)
      return bufferedImageRegion;
    if (region instanceof EllipsoidRegion ellipsoidRegion)
      return RegionRenders.of(ellipsoidRegion);
    if (region instanceof BallRegion ballRegion)
      return RegionRenders.of(ballRegion);
    if (region instanceof PolygonRegion polygonRegion)
      return PolygonRegionRender.of(polygonRegion);
    if (region instanceof RnPointcloudRegion rnPointcloudRegion)
      return new RnPointcloudRegionRender(rnPointcloudRegion);
    throw new RuntimeException();
  }

  public static RenderInterface create(TrajectoryRegionQuery trajectoryRegionQuery) {
    if (trajectoryRegionQuery instanceof StateTimeCollector)
      return new StateTimeCollectorRender((StateTimeCollector) trajectoryRegionQuery);
    throw new RuntimeException();
  }

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, Predicate<Tensor> region) {
    if (region instanceof ConeRegion coneRegion)
      new ConeRegionRender(coneRegion).render(geometricLayer, graphics);
    if (region instanceof BallRegion ballRegion)
      new BallRegionRender(ballRegion).render(geometricLayer, graphics);
  }

  public static RenderInterface createImageRegionRender(ImageRegion imageRegion) {
    return new ImageRender(image(imageRegion.image()), imageRegion.coordinateBounds());
  }
}
