// code by jph
package ch.alpine.owl.bot.se2.rrts;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.rrts.core.TransitionWrap;
import ch.alpine.sophus.clt.Clothoid;
import ch.alpine.sophus.clt.ClothoidBuilder;
import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.clt.ClothoidSampler;
import ch.alpine.sophus.clt.LagrangeQuadraticD;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.alg.UnitVector;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.mat.Tolerance;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.QuantityUnit;
import ch.alpine.tensor.qty.Unit;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Clips;
import ch.alpine.tensor.sca.Sign;

public class ClothoidTransitionTest {
  private static final ClothoidBuilder CLOTHOID_BUILDER = ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder();

  @Test
  public void testSimple() throws ClassNotFoundException, IOException {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = Serialization.copy(ClothoidTransition.of(CLOTHOID_BUILDER, start, end));
    LagrangeQuadraticD lagrangeQuadraticD = clothoidTransition.clothoid().curvature();
    Scalar head = lagrangeQuadraticD.head();
    Clips.interval(2.5, 2.6).requireInside(head);
  }

  @Test
  public void testLog2Int() {
    int value = 1024;
    int bit = 31 - Integer.numberOfLeadingZeros(value);
    assertEquals(bit, 10);
  }

  @Test
  public void testWrapped() {
    Tensor start = Tensors.vector(2, 3, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    TransitionWrap transitionWrap = clothoidTransition.wrapped(RealScalar.of(0.2));
    assertEquals(transitionWrap.samples().length(), transitionWrap.spacing().length());
    assertTrue(transitionWrap.spacing().stream().map(Scalar.class::cast).allMatch(Sign::isPositive));
  }

  @Test
  public void testSingularPoint() {
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(0, 0, 0);
    Clothoid clothoid = CLOTHOID_BUILDER.curve(start, end);
    LagrangeQuadraticD lagrangeQuadraticD = clothoid.curvature();
    // System.out.println(lagrangeQuadraticD.apply(RealScalar.ZERO));
    // System.out.println(lagrangeQuadraticD.apply(RealScalar.ONE));
    assertTrue(lagrangeQuadraticD.isZero(Tolerance.CHOP));
    Tensor vector = ClothoidSampler.samples(clothoid, RealScalar.of(0.1));
    assertEquals(vector, UnitVector.of(2, 1));
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    assertEquals(clothoidTransition.linearized(RealScalar.of(0.1)), Array.zeros(2, 3));
  }

  @Test
  public void testSamples2() {
    Tensor start = Tensors.vector(0, 0, 0);
    Tensor end = Tensors.vector(4, 0, 0);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    Chop._12.requireClose(clothoidTransition.length(), RealScalar.of(4));
    assertEquals(clothoidTransition.sampled(RealScalar.of(2.1)).length(), 2);
    assertEquals(clothoidTransition.sampled(RealScalar.of(1.9)).length(), 3);
  }

  @Test
  public void testSamplesSteps() {
    Tensor start = Tensors.vector(1, 2, 3);
    Tensor end = Tensors.vector(4, 1, 5);
    ClothoidTransition clothoidTransition = ClothoidTransition.of(CLOTHOID_BUILDER, start, end);
    assertEquals(clothoidTransition.sampled(RealScalar.of(0.2)).length(), 25);
    assertEquals(clothoidTransition.sampled(RealScalar.of(0.1)).length(), 50);
    {
      int val = clothoidTransition.linearized(RealScalar.of(0.2)).length();
      // System.out.println(val);
      assertTrue(10 < val && val < 30);
    }
    {
      int val = clothoidTransition.linearized(RealScalar.of(0.1)).length();
      // System.out.println(val);
      assertTrue(10 < val && val < 60);
    }
  }

  @Test
  public void testLinearize() {
    Clothoid clothoid = CLOTHOID_BUILDER.curve( //
        Tensors.fromString("{0.3[m], 1[m], 0}"), Tensors.fromString("{2[m], 2[m], .3}"));
    ClothoidTransition clothoidTransition = ClothoidTransition.of(clothoid);
    assertEquals(QuantityUnit.of(clothoid.length()), Unit.of("m"));
    clothoidTransition.linearized(Quantity.of(0.2, "m"));
  }

  @Test
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
