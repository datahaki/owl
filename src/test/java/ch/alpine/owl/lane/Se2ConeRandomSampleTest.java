// code by jph
package ch.alpine.owl.lane;

import java.io.IOException;

import ch.alpine.owl.bot.se2.Se2ComboRegion;
import ch.alpine.owl.math.region.Region;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class Se2ConeRandomSampleTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor apex = Tensors.vector(-10, -92, -3);
    Scalar semi = RealScalar.of(0.3);
    Scalar heading = RealScalar.ONE;
    Scalar depth = RationalScalar.HALF;
    RandomSampleInterface randomSampleInterface = Serialization.copy(Se2ConeRandomSample.of(apex, semi, heading, depth));
    Region<Tensor> region = Se2ComboRegion.cone(apex, semi, heading);
    for (int index = 0; index < 20; ++index) {
      Tensor randomSample = RandomSample.of(randomSampleInterface);
      assertTrue(region.isMember(randomSample));
    }
  }
}
