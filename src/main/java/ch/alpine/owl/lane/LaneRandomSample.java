// code by gjoel
package ch.alpine.owl.lane;

import java.io.Serializable;
import java.util.random.RandomGenerator;

import ch.alpine.sophis.crv.d2.Extract2D;
import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.pdf.RandomVariate;

public class LaneRandomSample implements RandomSampleInterface, Serializable {
  /** @param laneInterface
   * @param distribution
   * @return */
  public static LaneRandomSample of(LaneInterface laneInterface, Distribution distribution) {
    return new LaneRandomSample(laneInterface, distribution);
  }

  // ---
  private final LaneInterface laneInterface;
  private final Distribution distribution;

  private LaneRandomSample(LaneInterface laneInterface, Distribution distribution) {
    this.laneInterface = laneInterface;
    this.distribution = distribution;
  }

  @Override // from RandomSampleInterface
  public Tensor randomSample(RandomGenerator random) {
    return around(random.nextInt(laneInterface.midLane().length())).randomSample(random);
  }

  private RandomSampleInterface around(int index) {
    return around(laneInterface.midLane().get(index), laneInterface.margins().Get(index));
  }

  private RandomSampleInterface around(Tensor point, Scalar radius) {
    RandomSampleInterface randomSampleInterface = //
        BallRandomSample.of(Extract2D.FUNCTION.apply(point).maps(Scalar::zero), radius);
    return new RandomSampleInterface() {
      @Override // from RandomSampleInterface
      public Tensor randomSample(RandomGenerator random) {
        Tensor trans = randomSampleInterface.randomSample(random);
        Scalar rot = RandomVariate.of(distribution, random);
        return Se2Group.INSTANCE.combine(point, trans.append(rot));
      }
    };
  }
}
