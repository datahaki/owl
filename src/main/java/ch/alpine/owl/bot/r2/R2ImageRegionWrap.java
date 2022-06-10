// code by jph
package ch.alpine.owl.bot.r2;

import java.awt.image.BufferedImage;
import java.util.stream.Stream;

import ch.alpine.owl.glc.core.CostFunction;
import ch.alpine.owl.math.region.BufferedImageRegion;
import ch.alpine.owl.math.region.ImageRegion;
import ch.alpine.owl.util.img.FloodFill2D;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.api.RegionBoundsInterface;
import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.io.ImageFormat;
import ch.alpine.tensor.opt.nd.CoordinateBoundingBox;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.sca.Clips;

/** utility class that generates from a given image
 * 1) {@link ImageRegion}, and
 * 2) {@link CostFunction} with given radius */
public class R2ImageRegionWrap implements RegionBoundsInterface {
  private final Region<Tensor> imageRegion;
  private final CostFunction costFunction;
  private final Tensor range;

  /** @param image
   * @param range
   * @param ttl time to live */
  public R2ImageRegionWrap(BufferedImage bufferedImage, Tensor range, int ttl) {
    CoordinateBoundingBox coordinateBoundingBox = CoordinateBoundingBox.of(Stream.of( //
        Clips.positive(range.Get(0)), Clips.positive(range.Get(1))));
    imageRegion = new BufferedImageRegion(bufferedImage, coordinateBoundingBox, false);
    Tensor cost = FloodFill2D.of(ImageFormat.from(bufferedImage), ttl);
    costFunction = new DenseImageCostFunction(cost.divide(DoubleScalar.of(ttl)), range, RealScalar.ZERO);
    this.range = range.unmodifiable();
  }

  public Region<Tensor> region() {
    return imageRegion;
  }

  public CostFunction costFunction() {
    return costFunction;
  }

  public Tensor range() {
    return range;
  }

  @Override
  public CoordinateBoundingBox coordinateBounds() {
    return CoordinateBounds.of(range.map(Scalar::zero), range);
  }
}
