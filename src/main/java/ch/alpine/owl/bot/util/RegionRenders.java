// code by jph
package ch.alpine.owl.bot.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import ch.alpine.owl.bot.rn.RnPointcloudRegion;
import ch.alpine.owl.bot.rn.RnPointcloudRegionRender;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.region.EllipseRegionRender;
import ch.alpine.owl.gui.region.ImageRegionRender;
import ch.alpine.owl.gui.region.ImageRender;
import ch.alpine.owl.gui.region.PolygonRegionRender;
import ch.alpine.owl.gui.region.StateTimeCollectorRender;
import ch.alpine.owl.gui.ren.BallRegionRender;
import ch.alpine.owl.gui.ren.ConeRegionRender;
import ch.alpine.owl.gui.win.GeometricLayer;
import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.math.region.BufferedImageRegion;
import ch.alpine.owl.math.region.ConeRegion;
import ch.alpine.owl.math.region.EllipsoidRegion;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.math.region.PolygonRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTimeCollector;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.MatrixQ;
import ch.alpine.tensor.io.ImageFormat;

public enum RegionRenders {
  ;
  /** raster value 230 get's mapped to color {244, 244, 244, 255}
   * when using getRGB because of the color model attached to the
   * image type grayscale */
  public static final int RGB = 230;
  /** default color for obstacle region */
  public static final Color COLOR = new Color(RGB, RGB, RGB);
  public static final Color BOUNDARY = new Color(192, 192, 192);
  // ---
  private static final Scalar TFF = RealScalar.of(255);
  private static final Scalar OBS = RealScalar.of(RGB);

  static Scalar color(Scalar scalar) {
    return Scalars.isZero(scalar) ? TFF : OBS;
  }

  /** @param image with rank 2
   * @return */
  public static BufferedImage image(Tensor image) {
    return ImageFormat.of(MatrixQ.require(image).map(RegionRenders::color));
  }

  /** @param region
   * @return new instance of {@link RenderInterface} that visualizes given region,
   * or null if drawing capability is not available for the region */
  public static RenderInterface create(Region<Tensor> region) {
    if (region instanceof ImageRegion)
      return ImageRegionRender.create((ImageRegion) region);
    if (region instanceof BufferedImageRegion) {
      BufferedImageRegion bufferedImageRegion = (BufferedImageRegion) region;
      Tensor tensor = ImageFormat.from(bufferedImageRegion.bufferedImage());
      return ImageRender.of(image(tensor), bufferedImageRegion.pixel2model());
    }
    if (region instanceof EllipsoidRegion)
      return EllipseRegionRender.of((EllipsoidRegion) region);
    if (region instanceof BallRegion)
      return EllipseRegionRender.of((BallRegion) region);
    if (region instanceof PolygonRegion)
      return new PolygonRegionRender((PolygonRegion) region);
    if (region instanceof RnPointcloudRegion)
      return new RnPointcloudRegionRender((RnPointcloudRegion) region);
    throw new RuntimeException();
  }

  public static RenderInterface create(TrajectoryRegionQuery trajectoryRegionQuery) {
    if (trajectoryRegionQuery instanceof StateTimeCollector)
      return new StateTimeCollectorRender((StateTimeCollector) trajectoryRegionQuery);
    throw new RuntimeException();
  }

  public static void draw(GeometricLayer geometricLayer, Graphics2D graphics, Region<Tensor> region) {
    if (region instanceof ConeRegion)
      ConeRegionRender.draw(geometricLayer, graphics, (ConeRegion) region);
    if (region instanceof BallRegion)
      BallRegionRender.draw(geometricLayer, graphics, (BallRegion) region);
  }
}
