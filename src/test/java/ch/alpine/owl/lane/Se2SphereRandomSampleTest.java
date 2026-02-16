// code by jph
package ch.alpine.owl.lane;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;

class Se2SphereRandomSampleTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    Tensor apex = Tensors.vector(10, 20, 3);
    Scalar radius = Rational.HALF;
    Scalar heading = RealScalar.ONE;
    RandomSampleInterface randomSampleInterface = Serialization.copy(Se2SphereRandomSample.of(apex, radius, heading));
    MemberQ region = Se2ComboRegion.ball(apex, Tensors.of(radius, radius, heading));
    for (int index = 0; index < 20; ++index)
      assertTrue(region.test(RandomSample.of(randomSampleInterface)));
  }
}
