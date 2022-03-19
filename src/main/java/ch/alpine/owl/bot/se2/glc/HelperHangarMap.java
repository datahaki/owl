// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.awt.image.BufferedImage;

import ch.alpine.owl.bot.r2.ImageEdges;
import ch.alpine.owl.bot.r2.ImageRegions;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.io.ResourceData;

public class HelperHangarMap {
  static final Tensor MODEL2PIXEL = Tensors.matrixDouble(new double[][] { { 7.5, 0, 0 }, { 0, -7.5, 640 }, { 0, 0, 1 } });
  // ---
  public final BufferedImage bufferedImage;
  public final ImageRegion imageRegion;
  public final Region<Tensor> region;

  public HelperHangarMap(String string, CarEntity gokartEntity) {
    final Scalar scale = RealScalar.of(7.5); // meter_to_pixel
    Tensor tensor = ImageRegions.grayscale(ResourceData.of(string));
    bufferedImage = ImageFormat.of(tensor);
    tensor = ImageEdges.extrusion(tensor, 6); // == 0.73 * 7.5 == 5.475
    Tensor range = Tensors.vector(Dimensions.of(tensor)).divide(scale);
    imageRegion = new ImageRegion(tensor, range, false);
    region = Se2PointsVsRegions.line(gokartEntity.coords_X(), imageRegion);
  }
}
