// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.io.IOException;

import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.sophus.crv.dubins.DubinsPathComparators;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class DubinsTransitionTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionSpace transitionSpace = Serialization.copy(DubinsTransitionSpace.of(RealScalar.of(2), DubinsPathComparators.LENGTH));
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(3, -8, 1);
    Transition transition = transitionSpace.connect(start, end);
    TransitionWrap transitionWrap = transition.wrapped(RealScalar.of(0.3));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
    assertTrue(transitionWrap.spacing().stream().map(Scalar.class::cast).allMatch(Sign::isPositive));
  }

  public void testTrivial() {
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.of(1), DubinsPathComparators.LENGTH);
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(4, 0, 0);
    Transition transition = transitionSpace.connect(start, end);
    Tensor sampled = transition.sampled(RealScalar.of(2));
    Chop._12.requireClose(sampled, Tensors.fromString("{{2, 0, 0}, {4, 0, 0}}"));
  }

  public void testTrivial2() {
    TransitionSpace transitionSpace = DubinsTransitionSpace.of(RealScalar.of(1), DubinsPathComparators.LENGTH);
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(4, 0, 0);
    Transition transition = transitionSpace.connect(start, end);
    Tensor sampled = transition.sampled(RealScalar.of(1.9));
    Chop._12.requireClose(sampled, Tensors.fromString("{{4/3, 0, 0}, {8/3, 0, 0}, {4, 0, 0}}"));
  }
}
