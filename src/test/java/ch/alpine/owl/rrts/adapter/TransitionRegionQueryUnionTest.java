// code by jph
package ch.alpine.owl.rrts.adapter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.BallRegion;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophus.crv.Transition;
import ch.alpine.sophus.lie.rn.RnTransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;

class TransitionRegionQueryUnionTest {
  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionRegionQuery trq1 = //
        new SampledTransitionRegionQuery(new BallRegion(Tensors.vector(0, 0), RealScalar.ONE), RealScalar.of(0.1));
    TransitionRegionQuery trq2 = //
        new SampledTransitionRegionQuery(new BallRegion(Tensors.vector(2, 0), RealScalar.ONE), RealScalar.of(0.1));
    TransitionRegionQuery transitionRegionQuery = Serialization.copy(TransitionRegionQueryUnion.wrap(trq1, trq2));
    {
      Transition transition = RnTransitionSpace.INSTANCE.connect( //
          Tensors.vector(-2, 0), //
          Tensors.vector(-2, 1));
      assertTrue(transitionRegionQuery.isDisjoint(transition));
      assertTrue(trq1.isDisjoint(transition));
      assertTrue(trq2.isDisjoint(transition));
    }
    {
      Transition transition = RnTransitionSpace.INSTANCE.connect( //
          Tensors.vector(0, -2), //
          Tensors.vector(0, 2));
      assertFalse(transitionRegionQuery.isDisjoint(transition));
      assertFalse(trq1.isDisjoint(transition));
      assertTrue(trq2.isDisjoint(transition));
    }
    {
      Transition transition = RnTransitionSpace.INSTANCE.connect( //
          Tensors.vector(2, -2), //
          Tensors.vector(2, 2));
      assertFalse(transitionRegionQuery.isDisjoint(transition));
      assertTrue(trq1.isDisjoint(transition));
      assertFalse(trq2.isDisjoint(transition));
    }
  }
}
