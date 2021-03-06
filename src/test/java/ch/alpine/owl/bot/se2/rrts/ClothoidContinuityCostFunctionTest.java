// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.owl.rrts.adapter.EmptyTransitionRegionQuery;
import ch.alpine.owl.rrts.core.DefaultRrts;
import ch.alpine.owl.rrts.core.Rrts;
import ch.alpine.owl.rrts.core.RrtsNode;
import ch.alpine.owl.rrts.core.Transition;
import ch.alpine.owl.rrts.core.TransitionSpace;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;
import junit.framework.TestCase;

public class ClothoidContinuityCostFunctionTest extends TestCase {
  public void testSimple() {
    RrtsNode root = RrtsNode.createRoot(Tensors.vector(1, 2, 3), RealScalar.ONE);
    Tensor connect = Tensors.vector(10, 3, 2);
    RrtsNode next = root.connectTo(connect, RealScalar.of(2));
    Transition transition = ClothoidTransitionSpace.ANALYTIC.connect(connect, Tensors.vector(20, 5, 4));
    Scalar scalar = ClothoidContinuityCostFunction.INSTANCE.cost(next, transition);
    Chop._01.requireClose(scalar, RealScalar.of(0.8087544412507175));
  }

  public void testStraight() {
    RrtsNode root = RrtsNode.createRoot(Tensors.vector(1, 2, 0), RealScalar.ONE);
    Tensor connect = Tensors.vector(10, 2, 0);
    RrtsNode next = root.connectTo(connect, RealScalar.of(2));
    Transition transition = ClothoidTransitionSpace.ANALYTIC.connect(connect, Tensors.vector(20, 2, 0));
    Scalar scalar = ClothoidContinuityCostFunction.INSTANCE.cost(next, transition);
    Chop._12.requireClose(scalar, RealScalar.of(0));
  }

  public void testFunction1() {
    Scalar cost = ClothoidContinuityCostFunction.transitionCost( //
        Tensors.fromString("{0[m], 0[m], 0}"), //
        Tensors.fromString("{1[m], 1[m], " + Math.PI / 2 + "}"), //
        Tensors.fromString("{0[m], 2[m], " + Math.PI + "}"));
    Chop._12.requireZero(cost);
  }

  public void testFunction2() {
    Scalar cost = ClothoidContinuityCostFunction.transitionCost( //
        Tensors.fromString("{0[m], 0[m], 0}"), //
        Tensors.fromString("{1[m], 0.4[m], " + Math.PI / 2 + "}"), //
        Tensors.fromString("{0[m], 2[m], " + Math.PI + "}"));
    assertTrue(Scalars.lessThan(Scalars.fromString("7[m^-2]"), cost));
  }

  public void testSingle() {
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    Rrts rrts = new DefaultRrts( //
        transitionSpace, //
        Se2RrtsNodeCollections.of(transitionSpace, Tensors.vector(0, 0), Tensors.vector(10, 10)), //
        EmptyTransitionRegionQuery.INSTANCE, ClothoidContinuityCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0, 0), 0);
    RrtsNode n1 = rrts.insertAsNode(Tensors.vector(1, 1, Math.PI / 2), 0).get();
    assertEquals(RealScalar.ZERO, n1.costFromRoot());
  }

  public void testMultiple() {
    TransitionSpace transitionSpace = ClothoidTransitionSpace.ANALYTIC;
    Rrts rrts = new DefaultRrts( //
        transitionSpace, //
        Se2RrtsNodeCollections.of(transitionSpace, Tensors.vector(0, 0), Tensors.vector(10, 10)), //
        EmptyTransitionRegionQuery.INSTANCE, ClothoidContinuityCostFunction.INSTANCE);
    rrts.insertAsNode(Tensors.vector(0, 0, 0), 0);
    rrts.insertAsNode(Tensors.vector(1, 0, 0), 0);
    rrts.insertAsNode(Tensors.vector(2, 0, 0), 0);
    RrtsNode n3 = rrts.insertAsNode(Tensors.vector(3, 1, Math.PI / 2), 0).get();
    Chop._03.requireClose(RealScalar.ONE, n3.costFromRoot());
  }
}
