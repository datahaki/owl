// code by jph
package ch.alpine.owl.lane;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class Se2SphereRandomSampleTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor apex = Tensors.vector(10, 20, 3);
    Scalar radius = RationalScalar.HALF;
    Scalar heading = RealScalar.ONE;
    RandomSampleInterface randomSampleInterface = Serialization.copy(Se2SphereRandomSample.of(apex, radius, heading));
    Region<Tensor> region = Se2ComboRegion.ball(apex, Tensors.of(radius, radius, heading));
    for (int index = 0; index < 20; ++index)
      assertTrue(region.test(RandomSample.of(randomSampleInterface)));
  }
}
