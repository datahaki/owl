// code by jph
package ch.alpine.owl.math.noise;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.pdf.BinCounts;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;
import junit.framework.TestCase;

public class SimplexContinuousNoiseTest extends TestCase {
  public void testSimple() {
    Tensor noise = Tensors.vector(i -> DoubleScalar.of(10 * (1 + SimplexContinuousNoise.FUNCTION.at(0.1 * i, 0.1 + i))), 1000);
    Tensor bins = BinCounts.of(noise);
    assertEquals(bins.length(), 20);
    long len = bins.stream() //
        .map(Scalar.class::cast) //
        .filter(scalar -> Scalars.lessThan(DoubleScalar.of(30), scalar)) //
        .count();
    assertTrue(10 < len);
  }

  public void testExample() {
    double value = SimplexContinuousNoise.FUNCTION.at(0.3, 300.3, -600.5);
    assertEquals(value, -0.12579872366423636);
  }

  public void testMulti1() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    Clip clip = Clips.absoluteOne();
    for (int index = 0; index < 1000; ++index) {
      double vx = RandomVariate.of(distribution).number().doubleValue();
      double number = SimplexContinuousNoise.FUNCTION.at(vx);
      clip.requireInside(RealScalar.of(number));
    }
  }

  public void testMulti3() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    Clip clip = Clips.absoluteOne();
    for (int index = 0; index < 1000; ++index) {
      double vx = RandomVariate.of(distribution).number().doubleValue();
      double vy = RandomVariate.of(distribution).number().doubleValue();
      double vz = RandomVariate.of(distribution).number().doubleValue();
      double number = SimplexContinuousNoise.FUNCTION.at(vx, vy, vz);
      clip.requireInside(RealScalar.of(number));
    }
  }

  public void testMulti4() {
    Distribution distribution = UniformDistribution.of(-10, 10);
    Clip clip = Clips.absoluteOne();
    for (int index = 0; index < 1000; ++index) {
      double vx = RandomVariate.of(distribution).number().doubleValue();
      double vy = RandomVariate.of(distribution).number().doubleValue();
      double vz = RandomVariate.of(distribution).number().doubleValue();
      double va = RandomVariate.of(distribution).number().doubleValue();
      double number = SimplexContinuousNoise.at(vx, vy, vz, va);
      clip.requireInside(RealScalar.of(number));
    }
  }
}
