// code by jph
package ch.alpine.owl.math.noise;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.opt.nd.NdBox;
import ch.alpine.tensor.red.ArgMin;

public class BiasedBoxRandomSample implements RandomSampleInterface, Serializable {
  private final RandomSampleInterface box;
  private final int draws;

  public BiasedBoxRandomSample(NdBox ndBox, int draws) {
    box = BoxRandomSample.of(ndBox);
    this.draws = Integers.requirePositive(draws);
  }

  @Override
  public Tensor randomSample(Random random) {
    Tensor tensor = RandomSample.of(box, draws);
    Tensor result = Tensor.of(tensor.stream().map(SimplexContinuousNoise.FUNCTION));
    int argMin = ArgMin.of(result);
    return tensor.get(argMin);
  }
}
