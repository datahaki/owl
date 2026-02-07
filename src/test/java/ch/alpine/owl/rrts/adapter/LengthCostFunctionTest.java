// code by gjoel
package ch.alpine.owl.rrts.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.rn.rrts.RnRrtsNodeCollection;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.sophis.ts.RnTransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

class LengthCostFunctionTest {
  @Test
  void testSingle() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        new RnRrtsNodeCollection(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(10, 10))), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 0), 0).get();
    assertEquals(RealScalar.ONE, n1.costFromRoot());
  }

  @Test
  void testMultiple() {
    Rrts rrts = new DefaultRrts( //
        RnTransitionSpace.INSTANCE, //
        new RnRrtsNodeCollection(CoordinateBounds.of(Tensors.vector(0, 0), Tensors.vector(10, 10))), //
        EmptyTransitionRegionQuery.INSTANCE, LengthCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0), 0);
    rrts.insertAsNode(Tensors.vector(1, 0), 0);
    rrts.insertAsNode(Tensors.vector(2, 0), 0);
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(2, 1), 0).get();
    assertEquals(RealScalar.of(3), n3.costFromRoot());
  }
}
