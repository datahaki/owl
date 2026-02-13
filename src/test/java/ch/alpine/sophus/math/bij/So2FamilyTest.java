// code by jph
package ch.alpine.sophus.math.bij;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.OrthogonalMatrixQ;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class So2FamilyTest {
  @Test
  void testSimple() {
    BijectionFamily bijectionFamily = new So2Family(s -> RealScalar.of(5).subtract(s));
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.forward(scalar).apply(point);
      Chop._12.requireClose(bijectionFamily.inverse(scalar).apply(fwd), point);
    }
  }

  @Test
  void testReverse() {
    BijectionFamily bijectionFamily = new So2Family(s -> RealScalar.of(1.2).multiply(s));
    Distribution distribution = NormalDistribution.standard();
    for (int index = 0; index < 100; ++index) {
      Scalar scalar = RandomVariate.of(distribution);
      Tensor point = RandomVariate.of(distribution, 2);
      Tensor fwd = bijectionFamily.inverse(scalar).apply(point);
      Chop._12.requireClose(bijectionFamily.forward(scalar).apply(fwd), point);
    }
  }

  @Test
  void testForwardSe2() {
    R2RigidFamily rigidFamily = new So2Family(s -> s);
    Tensor matrix = rigidFamily.forward_se2(RealScalar.ONE);
    assertTrue(new OrthogonalMatrixQ(Chop._14).test(matrix));
    assertEquals(matrix, Se2Matrix.of(Tensors.vector(0, 0, 1)));
  }
}
