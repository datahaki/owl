// code by jph, gjoel
package ch.alpine.owl.bot.se2.rrts;

import java.io.IOException;

import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Last;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class ClothoidTransitionSpaceTest extends TestCase {
  public void testLength() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.fromString("{1[m], 1[m], 0}");
    Tensor end = Tensors.fromString("{2[m], 2[m]}").append(Pi.HALF);
    Transition transition = Serialization.copy(ClothoidTransitionSpace.ANALYTIC).connect(start, end);
    Chop._04.requireClose(transition.length(), Quantity.of(Pi.HALF, "m"));
    assertEquals(start, transition.start());
    assertEquals(end, transition.end());
  }

  public void testSamples() {
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}");
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}");
    Transition transition = ClothoidTransitionSpace.ANALYTIC.connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      Tensor samples = transition.sampled(res);
      assertEquals(10, samples.length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(8))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(16)), res));
      assertNotSame(start, samples.get(0));
      Tolerance.CHOP.requireClose(end, Last.of(samples));
    }
    // {
    // Tensor samples = transition.sampled(8);
    // assertEquals(8, samples.length());
    // assertNotSame(start, samples.get(0));
    // assertEquals(end, Last.of(samples));
    // }
  }

  public void testWrap() {
    Tensor start = Tensors.fromString("{1[m], 2[m], 1}");
    Tensor end = Tensors.fromString("{1[m], 6[m], 3}");
    Transition transition = ClothoidTransitionSpace.ANALYTIC.connect(start, end);
    {
      Scalar res = Quantity.of(0.5, "m");
      TransitionWrap wrap = transition.wrapped(res);
      assertEquals(9, wrap.samples().length());
      assertTrue(Scalars.lessThan(res, transition.length().divide(RealScalar.of(9))));
      assertTrue(Scalars.lessThan(transition.length().divide(RealScalar.of(10)), res));
      assertNotSame(start, wrap.samples().get(0));
      Tolerance.CHOP.requireClose(end, Last.of(wrap.samples()));
      assertTrue(wrap.spacing().stream() //
          .map(Scalar.class::cast) //
          .map(Sign::requirePositive) //
          .allMatch(s -> Scalars.lessEquals(s, res)));
    }
    // {
    // Scalar res = Quantity.of(0.5, "m");
    // TransitionWrap wrap = transition.wrapped(res);
    // assertEquals(8, wrap.samples().length());
    // assertNotSame(start, wrap.samples().get(0));
    // assertEquals(end, Last.of(wrap.samples()));
    // wrap.spacing().extract(0, 8).stream().map(Tensor::Get) //
    // .map(Sign::requirePositive) //
    // .forEach(s -> Chop._01.requireClose(s, transition.length().divide(RealScalar.of(8))));
    // }
  }
}
