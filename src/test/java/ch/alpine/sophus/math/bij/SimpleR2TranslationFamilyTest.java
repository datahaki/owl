// code by jph
package ch.alpine.sophus.math.bij;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.d.DiscreteUniformDistribution;

class SimpleR2TranslationFamilyTest {
  @Test
  void testSimple() {
    BijectionFamily bijectionFamily = new SimpleR2TranslationFamily(s -> Tensors.of(RealScalar.of(3), RealScalar.of(100).add(s)));
    Distribution distribution = DiscreteUniformDistribution.of(-15, 16);
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.forward(scalar).apply(point);
      assertEquals(bijectionFamily.inverse(scalar).apply(fwd), point);
    }
  }
}
