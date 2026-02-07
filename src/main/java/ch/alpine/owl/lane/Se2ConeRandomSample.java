// code by gjoel, jph
package ch.alpine.owl.lane;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.rot.AngleVector;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.UniformDistribution;
import ch.alpine.tensor.sca.Clips;

/** @see Se2ComboRegion */
public class Se2ConeRandomSample implements RandomSampleInterface, Serializable {
  /** @param apex vector of the form {x, y, angle}
   * @param semi non-negative
   * @param heading non-negative
   * @param depth non-negative
   * @return */
  public static RandomSampleInterface of(Tensor apex, Scalar semi, Scalar heading, Scalar depth) {
    return new Se2ConeRandomSample( //
        apex, //
        UniformDistribution.of(Clips.absolute(semi)), //
        UniformDistribution.of(Clips.absolute(heading)), //
        UniformDistribution.of(Clips.positive(depth)));
  }

  // ---
  private final Tensor apex;
  private final Distribution distributionDepth;
  private final Distribution distributionAngle;
  private final Distribution distributionHeading;

  /** @param apex vector of the form {x, y, angle}
   * @param distributionSemi values not outside the interval [-pi, pi]
   * @param distributionHeading values not outside the interval [-pi, pi]
   * @param distributionRadius non-negative
   * @return */
  public Se2ConeRandomSample( //
      Tensor apex, //
      Distribution distributionSemi, //
      Distribution distributionHeading, //
      Distribution distributionDepth) {
    this.apex = apex;
    this.distributionAngle = distributionSemi;
    this.distributionHeading = distributionHeading;
    this.distributionDepth = distributionDepth;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(RandomGenerator randomGenerator) {
    Tensor xy = AngleVector.of(RandomVariate.of(distributionAngle, randomGenerator)).multiply(RandomVariate.of(distributionDepth, randomGenerator));
    return Se2Group.INSTANCE.combine(apex, xy.append(RandomVariate.of(distributionHeading, randomGenerator)));
  }
}
