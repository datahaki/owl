// code by jph, gjoel
package ch.alpine.owl.rrts.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.crv.Transition;
import ch.alpine.sophus.crv.TransitionWrap;
import ch.alpine.sophus.crv.clt.ClothoidTransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;

class ReversalTransitionSpaceTest {
  @Test
  void testLength() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.fromString("{1[m], 1[m]}").append(Pi.VALUE);
    Tensor end = Tensors.fromString("{2[m], 2[m]}").append(Pi.HALF.negate());
    Transition transition = Serialization.copy(ReversalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC)).connect(start, end);
    Chop._03.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  @Test
  void testSamples() {
    Tensor se2pi = Tensors.of(Quantity.of(0, "m"), Quantity.of(0, "m"), Pi.VALUE);
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}").add(se2pi);
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}").add(se2pi);
    Transition transition = ReversalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC).connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      Tensor samples = transition.sampled(res);
      assertEquals(10, samples.length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
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
  void testWrap() {
    Tensor se2pi = Tensors.of(Quantity.of(0, "m"), Quantity.of(0, "m"), Pi.VALUE);
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}").add(se2pi);
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}").add(se2pi);
    Transition transition = ReversalTransitionSpace.of(ClothoidTransitionSpace.ANALYTIC).connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(10, wrap.samples().length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start, wrap.samples().get(0));
      assertEquals(end, Last.of(wrap.samples()));
      assertTrue(wrap.spacing().extract(0, 10).stream().map(Scalar.class::cast) //
          .map(Sign::requirePositive) //
          .allMatch(s -> Scalars.lessEquals(s, res)));
    }
    // {
    // TransitionWrap wrap = transition.wrapped(8);
    // assertEquals(8, wrap.samples().length());
    // assertNotSame(start, wrap.samples().get(0));
    // assertEquals(end, Last.of(wrap.samples()));
    // wrap.spacing().extract(0, 8).stream().map(Tensor::Get) //
    // .map(Sign::requirePositive) //
    // .forEach(s -> Chop._01.requireClose(s, transition.length().divide(RealScalar.of(8))));
    // }
  }
}
