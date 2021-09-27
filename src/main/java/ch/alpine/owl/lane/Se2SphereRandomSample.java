// code by gjoel, jph
package ch.alpine.owl.lane;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.sophus.math.d2.Extract2D;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

/** @see Se2ComboRegion */
public class Se2SphereRandomSample implements RandomSampleInterface, Serializable {
  /** @param apex
   * @param radius non-negative
   * @param heading non-negative
   * @return */
  public static RandomSampleInterface of(Tensor apex, Scalar radius, Scalar heading) {
    return new Se2SphereRandomSample(apex, radius, UniformDistribution.of(Clips.absolute(heading)));
  }

  // ---
  private final Se2GroupElement se2GroupElement;
  private final RandomSampleInterface randomSampleInterface;
  private final Distribution distribution;

  /** @param apex vector of the form {x, y, angle}
   * @param radius non-negative
   * @param distribution for heading */
  public Se2SphereRandomSample(Tensor apex, Scalar radius, Distribution distribution) {
    se2GroupElement = new Se2GroupElement(apex);
    randomSampleInterface = BallRandomSample.of(Extract2D.FUNCTION.apply(apex).map(Scalar::zero), radius);
    this.distribution = distribution;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor xy = randomSampleInterface.randomSample(random);
    return se2GroupElement.combine(xy.append(RandomVariate.of(distribution, random)));
  }
}
