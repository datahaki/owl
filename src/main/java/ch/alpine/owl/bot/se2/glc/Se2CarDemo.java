// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public abstract class Se2CarDemo extends Se2Demo {
  /** probe coordinates specific for small car */
  private static final Tensor PROBE_X = Tensors.vector(0.2, 0.1, 0, -0.1);

  public static Region<Tensor> line(Region<Tensor> region) {
    return Se2PointsVsRegions.line(PROBE_X, region);
  }

  public static PlannerConstraint createConstraint(Region<Tensor> region) {
    return RegionConstraints.timeInvariant(line(region));
  }
}
