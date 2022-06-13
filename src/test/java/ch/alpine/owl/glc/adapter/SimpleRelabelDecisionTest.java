// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class SimpleRelabelDecisionTest {
  private static boolean doRelabel(Scalar newMerit, Scalar oldMerit, Scalar slack) {
    return ((SimpleRelabelDecision) SimpleRelabelDecision.with(slack)).doRelabel(newMerit, oldMerit);
  }

  private static void check(boolean a, boolean b, boolean c) {
    boolean r1 = a || (b && c);
    boolean r2 = a || b && c;
    assertEquals(r1, r2);
  }

  @Test
  void testSimple() {
    check(true, true, true);
    check(true, true, false);
    check(true, false, true);
    check(true, false, false);
    check(false, true, true);
    check(false, true, false);
    check(false, false, true);
    check(false, false, false);
  }

  @Test
  void testStatic() {
    assertTrue(doRelabel(RealScalar.of(1), RealScalar.of(2), DoubleScalar.of(2)));
    assertFalse(doRelabel(RealScalar.of(3), RealScalar.of(2), DoubleScalar.of(2)));
    assertFalse(doRelabel(RealScalar.of(1.), RealScalar.of(2), DoubleScalar.of(2)));
    assertTrue(doRelabel(RealScalar.of(1.), RealScalar.of(2), DoubleScalar.of(0.5)));
    assertFalse(doRelabel(RealScalar.of(1.9), RealScalar.of(2), DoubleScalar.of(0.5)));
    assertFalse(doRelabel(RealScalar.of(2.1), RealScalar.of(2), DoubleScalar.of(0.5)));
  }

  @Test
  void testQuantity() {
    assertTrue(doRelabel(Quantity.of(1, "USD"), Quantity.of(2, "USD"), DoubleScalar.of(0.01)));
    assertFalse(doRelabel(Quantity.of(3, "USD"), Quantity.of(2, "USD"), DoubleScalar.of(0.01)));
  }

  @Test
  void testChop() {
    Chop._05.requireZero(Quantity.of(1e-7, "USD"));
  }
}
