// code by jph
package ch.alpine.owl.bot.se2;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;

class Se2CoveringWrapTest {
  @Test
  void testMod2Pi() {
    double pa = -2 * Math.PI * 8;
    double qa = +2 * Math.PI + 0.1;
    Tensor p = Tensors.vector(20, -30, pa);
    Tensor q = Tensors.vector(20, -30, qa);
    Tensor tensor = Se2CoveringWrap.INSTANCE.difference(p, q);
    Chop._12.requireAllZero(tensor.extract(0, 2));
    Chop._14.requireClose(tensor.Get(2), RealScalar.of(qa - pa));
  }
}
