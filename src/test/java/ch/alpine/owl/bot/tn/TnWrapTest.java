// code by jph
package ch.alpine.owl.bot.tn;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.nrm.Vector2Norm;

class TnWrapTest {
  @Test
  void testSimple() {
    TnWrap tnWrap = new TnWrap(Tensors.vector(3, 7));
    Tensor r = tnWrap.represent(Tensors.vector(11, 20));
    assertEquals(r, Tensors.vector(2, 6));
  }

  private static Scalar _distance(TnWrap tnWrap, Tensor p, Tensor q) {
    Scalar d1 = Vector2Norm.of(tnWrap.difference(p, q));
    Scalar d2 = Vector2Norm.of(tnWrap.difference(q, p));
    assertEquals(d1, d2);
    return d1;
  }

  @Test
  void testDistance() {
    TnWrap tnWrap = new TnWrap(Tensors.vector(3, 7));
    assertEquals(_distance(tnWrap, Tensors.vector(0, 0), Tensors.vector(3, 7)), RealScalar.ZERO);
    assertEquals(_distance(tnWrap, Tensors.vector(0, 0), Tensors.vector(4, 7)), RealScalar.ONE);
    assertEquals(_distance(tnWrap, Tensors.vector(0, 0), Tensors.vector(2, 7)), RealScalar.ONE);
    assertEquals(_distance(tnWrap, Tensors.vector(0, 0), Tensors.vector(3, 8)), RealScalar.ONE);
    assertEquals(_distance(tnWrap, Tensors.vector(0, 0), Tensors.vector(3, 6)), RealScalar.ONE);
  }
}
