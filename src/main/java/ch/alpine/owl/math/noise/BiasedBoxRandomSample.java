// code by jph
package ch.alpine.owl.math.noise;

import java.io.Serializable;
import java.util.Random;

import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.ext.Integers;
import ch.alpine.tensor.opt.nd.Box;
import ch.alpine.tensor.red.ArgMin;

public class BiasedBoxRandomSample implements RandomSampleInterface, Serializable {
  private final RandomSampleInterface randomSampleInterface;
  private final int draws;

  public BiasedBoxRandomSample(Box box, int draws) {
    randomSampleInterface = BoxRandomSample.of(box);
    this.draws = Integers.requirePositive(draws);
  }

  @Override
  public Tensor randomSample(Random random) {
    Tensor tensor = RandomSample.of(randomSampleInterface, draws);
    Tensor result = Tensor.of(tensor.stream().map(SimplexContinuousNoise.FUNCTION));
    int argMin = ArgMin.of(result);
    return tensor.get(argMin);
  }
}
