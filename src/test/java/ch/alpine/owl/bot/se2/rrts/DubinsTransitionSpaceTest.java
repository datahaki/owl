// code by gjoel
package ch.alpine.owl.bot.se2.rrts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;

class DubinsTransitionSpaceTest {
  @Test
  public void testLengthUnitless() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.fromString("{1, 2}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2, 6, 0}");
    Transition transition = Serialization.copy(DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH).connect(start, end));
    assertEquals(RealScalar.of(3).add(Pi.HALF), transition.length());
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  @Test
  public void testLengthUnits() {
    Tensor start = Tensors.fromString("{1[m], 2[m]}").append(Pi.HALF);
    Tensor end = Tensors.fromString("{2[m], 6[m], 0}");
    Transition transition = DubinsTransitionSpace.of(Quantity.of(1, "m"), DubinsPathComparators.LENGTH).connect(start, end);
    assertEquals(Quantity.of(3 + Math.PI / 2, "m"), transition.length());
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  @Test
  public void testSamples() {
    Tensor start = Tensors.fromString("{2, 1, 0}");
    Tensor end = Tensors.fromString("{6, 1, 0}");
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH);
    Transition transition = transitionSpace.connect(start, end);
    {
      Scalar res = RationalScalar.HALF;
      Tensor samples = transition.sampled(res);
      assertEquals(8, samples.length());
      assertNotSame(start, samples.get(0));
      assertEquals(end, Last.of(samples));
    }
    // {
    // Tensor samples = transition.sampled(8);
    // assertEquals(8, samples.length());
    // assertNotSame(start, samples.get(0));
    // assertEquals(end, Last.of(samples));
    // }
  }

  @Test
  public void testWrap() {
    Tensor start = Tensors.fromString("{2, 1, 0}");
    Tensor end = Tensors.fromString("{6, 1, 0}");
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.ONE, DubinsPathComparators.LENGTH);
    Transition transition = transitionSpace.connect(start, end);
    {
      Scalar res = RationalScalar.HALF;
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(8, wrap.samples().length());
      assertNotSame(start, wrap.samples().get(0));
      assertEquals(end, Last.of(wrap.samples()));
      wrap.spacing().stream().forEach(s -> assertEquals(res, s));
    }
    // {
    // TransitionWrap wrap = transition.wrapped(8);
    // assertEquals(8, wrap.samples().length());
    // assertNotSame(start, wrap.samples().get(0));
    // assertEquals(end, Last.of(wrap.samples()));
    // wrap.spacing().stream().forEach(s -> assertEquals(res, s));
    // }
  }

  @Test
  public void testRadiusFail() {
    assertThrows(Exception.class, () -> DubinsTransitionSpace.of(RealScalar.of(0.0), DubinsPathComparators.LENGTH));
    assertThrows(Exception.class, () -> DubinsTransitionSpace.of(RealScalar.of(-0.1), DubinsPathComparators.LENGTH));
  }

  @Test
  public void testComparatorFail() {
    DubinsTransitionSpace.of(RealScalar.of(1.0), DubinsPathComparators.LENGTH);
    assertThrows(Exception.class, () -> DubinsTransitionSpace.of(RealScalar.of(1.0), null));
  }
}
