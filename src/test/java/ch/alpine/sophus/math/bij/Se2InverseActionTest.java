// code by jph
package ch.alpine.sophus.math.bij;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.se2.Se2Matrix;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.mat.re.LinearSolve;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.sca.Chop;

class Se2InverseActionTest {
  @Test
  void testSimple() {
    Tensor xya = Tensors.vector(1, 2, 3);
    TensorUnaryOperator tensorUnaryOperator = new Se2InverseAction(xya);
    Tensor p = Tensors.vector(6, -9, 1);
    Tensor q1 = tensorUnaryOperator.apply(p);
    Tensor q2 = LinearSolve.of(Se2Matrix.of(xya), p).extract(0, 2);
    Chop._12.requireClose(q1, q2);
  }

  @Test
  void testPureSe2() {
    Distribution distribution = NormalDistribution.standard();
    Tensor p = RandomVariate.of(distribution, 3);
    Tensor q = RandomVariate.of(distribution, 3);
    Tolerance.CHOP.requireClose( //
        Se2Group.INSTANCE.diffOp(p).apply(q).extract(0, 2), //
        new Se2InverseAction(p).apply(q.extract(0, 2)));
  }

  @Test
  void testSerializable() throws ClassNotFoundException, IOException {
    Se2Bijection se2Bijection = new Se2Bijection(Tensors.vector(2, -3, 1.3));
    Serialization.copy(se2Bijection.inverse());
  }
}
