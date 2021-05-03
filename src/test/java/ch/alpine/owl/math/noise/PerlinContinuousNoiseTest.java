// code by jph
package ch.alpine.owl.math.noise;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.BinCounts;
import junit.framework.TestCase;

public class PerlinContinuousNoiseTest extends TestCase {
  public void testSimple() {
    Tensor noise = Tensors.vector(i -> DoubleScalar.of(10 * (1 + PerlinContinuousNoise.FUNCTION.at(1.6, .1 * i, .1 + i))), 1000);
    Tensor bins = BinCounts.of(noise);
    assertEquals(bins.length(), 18);
    long len = bins.stream() //
        .map(Scalar.class::cast) //
        .filter(scalar -> Scalars.lessThan(DoubleScalar.of(30), scalar)) //
        .count();
    assertTrue(5 < len);
  }

  public void testExample() {
    double value = PerlinContinuousNoise.FUNCTION.at(0.3, 300.3, -600.5);
    assertEquals(value, 0.04274652592000538);
  }
}
