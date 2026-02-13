// code by jph
package ch.alpine.owl.bot.se2;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.sophus.lie.so2.So2;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.mat.IdentityMatrix;
import ch.alpine.tensor.pdf.Distribution;
import ch.alpine.tensor.pdf.RandomVariate;
import ch.alpine.tensor.pdf.c.NormalDistribution;
import ch.alpine.tensor.red.Times;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Floor;

class Se2WrapTest {
  static Tensor convertToKey(Tensor eta, TensorUnaryOperator represent, Tensor x) {
    return Times.of(eta, represent.apply(x)).maps(Floor.FUNCTION);
  }

  @Test
  void testSe2xT_represent() {
    Tensor res = Se2Wrap.INSTANCE.represent(Tensors.vector(1, 1, 2 * Math.PI, 1));
    assertEquals(res, Tensors.vector(1, 1, 0, 1));
  }

  @Test
  void testMod2Pi_1() {
    Tensor p = Tensors.vector(20, -43, -2 * Math.PI * 8);
    Tensor q = Tensors.vector(20, -43, +2 * Math.PI + 0.1);
    Tensor distance = Se2Wrap.INSTANCE.difference(p, q);
    assumeTrue(false); // TODO OWL API changed?
    Chop._10.requireClose(distance, Tensors.vector(0, 0, 0.1));
  }

  @Test
  void testMod2Pi_2() {
    Tensor p = Tensors.vector(0, 0, -2 * Math.PI * 3);
    Tensor q = Tensors.vector(0, 0, +2 * Math.PI + 0.1);
    Tensor difference = Se2Wrap.INSTANCE.difference(p, q);
    assumeTrue(false); // TODO OWL API changed?
    Chop._13.requireClose(difference, Tensors.vector(0, 0, 0.1));
  }

  @Test
  void testMod2PiUnits() {
    Tensor p1 = Tensors.fromString("{20[m], -43[m]}").append(RealScalar.of(-2 * Math.PI * 3));
    Tensor p2 = Tensors.fromString("{20[m], -43[m]}").append(RealScalar.of(-2 * Math.PI * 8));
    Tensor q = Tensors.fromString("{21[m], -48[m]}").append(RealScalar.of(+2 * Math.PI + 0.1));
    Tensor d1 = Se2Wrap.INSTANCE.difference(p1, q);
    Tensor d2 = Se2Wrap.INSTANCE.difference(p2, q);
    assumeTrue(false); // TODO OWL API changed?
    Chop._08.requireClose(d1, d2);
  }

  @Test
  void testEndPoints() {
    Distribution distribution = NormalDistribution.of(0, 10);
    for (int index = 0; index < 100; ++index) {
      Tensor p = RandomVariate.of(distribution, 3);
      p.set(So2.MOD, 2);
      Tensor q = RandomVariate.of(distribution, 3);
      q.set(So2.MOD, 2);
      Chop._14.requireClose(p, Se2Group.INSTANCE.split(p, q, RealScalar.ZERO));
      Tensor r = Se2Group.INSTANCE.split(p, q, RealScalar.ONE);
      if (!Chop._14.isClose(q, r))
        Chop._10.requireAllZero(Se2Wrap.INSTANCE.difference(q, r));
    }
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> Se2Wrap.INSTANCE.represent(Tensors.vector(1, 2)));
  }

  @Test
  void testFailMatrix() {
    assertThrows(Exception.class, () -> Se2Wrap.INSTANCE.represent(IdentityMatrix.of(3)));
  }
}
