// code by jph
package ch.alpine.owl.bot.se2;

import java.util.Optional;

import ch.alpine.owl.math.pursuit.PurePursuit;
import ch.alpine.sophus.lie.se2c.Se2CoveringIntegrator;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.NormalDistribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.UniformDistribution;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class Se2CarLieIntegratorTest extends TestCase {
  public void testCombine() {
    for (int index = 0; index < 20; ++index) {
      Tensor g = RandomVariate.of(NormalDistribution.standard(), 3);
      Tensor x = RandomVariate.of(NormalDistribution.standard(), 3);
      x.set(RealScalar.ZERO, 1);
      Chop._10.requireClose( //
          Se2CoveringIntegrator.INSTANCE.spin(g, x), //
          Se2CarLieIntegrator.INSTANCE.spin(g, x));
    }
  }

  public void testRatioPositiveX() {
    for (Tensor lookAhead : Tensors.of(Tensors.vector(3, 1), Tensors.vector(3, -1))) {
      Optional<Scalar> optional = PurePursuit.ratioPositiveX(lookAhead);
      Scalar ratio = optional.get();
      Scalar speed = RealScalar.of(3.217506); // through experimentation
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor tensor = Se2CarLieIntegrator.INSTANCE.spin(Array.zeros(3), u);
      Chop._06.requireClose(tensor.extract(0, 2), lookAhead);
    }
  }

  public void testCreateLookAhead() {
    Distribution distribution = UniformDistribution.of(-0.3, +0.3);
    Distribution speeds = UniformDistribution.of(0, 3);
    for (Tensor _ratio : RandomVariate.of(distribution, 100)) {
      Scalar ratio = (Scalar) _ratio;
      Scalar speed = RandomVariate.of(speeds);
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor lookAhead = Se2CarLieIntegrator.INSTANCE.spin(Array.zeros(3), u);
      Optional<Scalar> optional = PurePursuit.ratioPositiveX(lookAhead);
      Scalar scalar = optional.get();
      Chop._05.requireClose(ratio, scalar);
    }
  }

  public void testRatioNegativeX() {
    for (Tensor lookAhead : Tensors.of(Tensors.vector(-3, 1), Tensors.vector(-3, -1))) {
      Optional<Scalar> optional = PurePursuit.ratioNegativeX(lookAhead);
      Scalar ratio = optional.get();
      Scalar speed = RealScalar.of(-3.217506); // through experimentation
      Tensor u = Tensors.of(speed, RealScalar.ZERO, ratio.multiply(speed));
      Tensor tensor = Se2CarLieIntegrator.INSTANCE.spin(Array.zeros(3), u);
      Chop._06.requireClose(tensor.extract(0, 2), lookAhead);
    }
  }
}
