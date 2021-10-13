// code by gjoel, jph
package ch.alpine.owl.lane;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
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
  private final Se2GroupElement se2GroupElement;
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
    se2GroupElement = new Se2GroupElement(apex);
    this.distributionAngle = distributionSemi;
    this.distributionHeading = distributionHeading;
    this.distributionDepth = distributionDepth;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(Random random) {
    Tensor xy = AngleVector.of(RandomVariate.of(distributionAngle, random)).multiply(RandomVariate.of(distributionDepth, random));
    return se2GroupElement.combine(xy.append(RandomVariate.of(distributionHeading, random)));
  }
}
