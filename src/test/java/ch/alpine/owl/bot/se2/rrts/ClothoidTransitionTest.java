// code by jph
package ch.alpine.owl.bot.se2.rrts;

import java.io.IOException;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.clt.LagrangeQuadraticD;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class ClothoidTransitionTest extends TestCase {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = Serialization.copy(ClothoidTransition.of(CLOTHOID_BUILDER, start, end));
    LagrangeQuadraticD lagrangeQuadraticD = clothoidTransition.clothoid().curvature();
    Scalar head = lagrangeQuadraticD.head();
    Clips.interval(2.5, 2.6).requireInside(head);
  }

  public void testLog2Int() {
    int value = 1024;
    int bit = 31 - Integer.numberOfLeadingZeros(value);
    assertEquals(bit, 10);
  }

  public void testWrapped() {
    Tensor start = Tensors.vector(2, 3, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    TransitionWrap transitionWrap = clothoidTransition.wrapped(RealScalar.of(0.2));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
    assertTrue(transitionWrap.spacing().stream().map(Scalar.class::cast).allMatch(Sign::isPositive));
  }

  public void testSingularPoint() {
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(0, 0, 0);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.1)), Array.zeros(2, 3));
  }

  public void testSamples2() {
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(4, 0, 0);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    Chop._12.requireClose(clothoidTransition.length(), RealScalar.of(4));
    assertEquals(clothoidTransition.sampled(RealScalar.of(2.1)).length(), 2);
    assertEquals(clothoidTransition.sampled(RealScalar.of(1.9)).length(), 3);
  }

  public void testSamplesSteps() {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    assertEquals(clothoidTransition.sampled(RealScalar.of(0.2)).length(), 25);
    assertEquals(clothoidTransition.sampled(RealScalar.of(0.1)).length(), 50);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.2)).length(), 26);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.1)).length(), 51);
  }

  public void testFails() {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    AssertFail.of(() -> clothoidTransition.sampled(RealScalar.of(-0.1)));
    AssertFail.of(() -> clothoidTransition.sampled(RealScalar.ZERO));
    AssertFail.of(() -> clothoidTransition.wrapped(RealScalar.ZERO));
    AssertFail.of(() -> clothoidTransition.linearized(RealScalar.ZERO));
  }
}
