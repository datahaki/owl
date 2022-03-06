// code by gjoel
package ch.alpine.owl.lane;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.sophus.hs.r2.Extract2D;
import ch.alpine.sophus.lie.se2.Se2GroupElement;
import ch.alpine.sophus.math.sample.BallRandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.pdf.Distribution;
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
  public Tensor randomSample(Random random) {
    return around(random.nextInt(laneInterface.midLane().length())).randomSample(random);
  }

  private RandomSampleInterface around(int index) {
    return around(laneInterface.midLane().get(index), laneInterface.margins().Get(index));
  }

  private RandomSampleInterface around(Tensor point, Scalar radius) {
    RandomSampleInterface randomSampleInterface = //
        BallRandomSample.of(Extract2D.FUNCTION.apply(point).map(Scalar::zero), radius);
    Se2GroupElement se2GroupElement = new Se2GroupElement(point);
    return new RandomSampleInterface() {
      @Override // from RandomSampleInterface
      public Tensor randomSample(Random random) {
        Tensor trans = randomSampleInterface.randomSample(random);
        Scalar rot = RandomVariate.of(distribution, random);
        return se2GroupElement.combine(trans.append(rot));
      }
    };
  }
}
