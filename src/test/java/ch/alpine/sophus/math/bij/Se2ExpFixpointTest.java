// code by jph
package ch.alpine.sophus.math.bij;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.sophus.lie.se2.Se2ExpFixpoint;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Subdivide;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class Se2ExpFixpointTest {
  @Test
  void testSimple() {
    Tensor velocity = Tensors.fromString("{3[m*s^-1], .2[m*s^-1], 0.3[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    for (Tensor _t : Subdivide.of(Quantity.of(-2.1, "s"), Quantity.of(10, "s"), 13)) {
      Se2Bijection se2Bijection = new Se2Bijection(Se2CoveringGroup.INSTANCE.exponential0().exp(velocity.multiply((Scalar) _t)));
      Chop._10.requireClose(se2Bijection.forward().apply(optional.orElseThrow()), optional.orElseThrow());
    }
  }

  @Test
  void testSimple2() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0.3[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    for (Tensor _t : Subdivide.of(Quantity.of(-5.1, "s"), Quantity.of(10, "s"), 17)) {
      Se2Bijection se2Bijection = new Se2Bijection(Se2CoveringGroup.INSTANCE.exponential0().exp(velocity.multiply((Scalar) _t)));
      Chop._10.requireClose(se2Bijection.forward().apply(optional.orElseThrow()), optional.orElseThrow());
    }
  }

  @Test
  void testEmpty() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity);
    assertFalse(optional.isPresent());
  }

  @Test
  void testEmptyChop() {
    Tensor velocity = Tensors.fromString("{-3[m*s^-1], 1.2[m*s^-1], -0.00003[s^-1]}");
    Optional<Tensor> optional = Se2ExpFixpoint.of(velocity, Chop._03);
    assertFalse(optional.isPresent());
  }
}
