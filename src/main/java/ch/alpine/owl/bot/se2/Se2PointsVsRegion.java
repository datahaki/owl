// code by jph
package ch.alpine.owl.bot.se2;

import java.util.Objects;
import java.util.function.Predicate;

import ch.alpine.sophus.math.bij.Se2Bijection;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.chq.MemberQ;

/** used in se2 animation demo to check if footprint of vehicle intersects with obstacle region */
/* package */ class Se2PointsVsRegion implements MemberQ {
  private final Tensor points;
  private final Predicate<Tensor> region;

  /** @param points n x 2
   * @param region */
  public Se2PointsVsRegion(Tensor points, Predicate<Tensor> region) {
    this.points = points;
    this.region = Objects.requireNonNull(region);
  }

  /** @param tensor of the form (x, y, theta)
   * @return true if any of the points subject to the given transformation are in region */
  @Override
  public boolean test(Tensor tensor) {
    Se2Bijection se2Bijection = new Se2Bijection(tensor);
    return points.stream().map(se2Bijection.forward()).anyMatch(region::test);
  }
}
