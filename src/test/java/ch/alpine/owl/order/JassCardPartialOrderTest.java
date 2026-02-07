// code by astoll
package ch.alpine.owl.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.order.OrderComparison;

class JassCardPartialOrderTest {
  final JassCard A = new JassCard(JassFarbe.ROSEN, JassType.ACE, true);
  final JassCard B = new JassCard(JassFarbe.ROSEN, JassType.JACK, true);
  final JassCard C = new JassCard(JassFarbe.EICHEL, JassType.QUEEN, false);
  final JassCard D = new JassCard(JassFarbe.EICHEL, JassType.SEVEN, false);
  final JassCard E = new JassCard(JassFarbe.SCHELLE, JassType.ACE, false);

  @Test
  void testAIsTrumpfBIsNot() {
    // since A is Trumpf and B is not: A > B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(A, C), OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  void testAIsNotTrumpfBIs() {
    // since B is Trumpf and A is not: A < B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(C, B), OrderComparison.STRICTLY_PRECEDES);
  }

  @Test
  void testAHigherThanBBothTrumpf() {
    // A has higher value and both are Trumpf: A > B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(B, A), OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  void testALowerThanBBothTrumpf() {
    // A has lower value and both are Trumpf: A < B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(A, B), OrderComparison.STRICTLY_PRECEDES);
  }

  @Test
  void testAHigherThanBNoneTrumpf() {
    // A has higher value and none are Trumpf: A > B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(C, D), OrderComparison.STRICTLY_SUCCEEDS);
  }

  @Test
  void testALowerThanBNoneTrumpf() {
    // A has lower value and none are Trumpf: A < B
    assertEquals(JassCardPartialOrder.INSTANCE.compare(D, C), OrderComparison.STRICTLY_PRECEDES);
  }

  @Test
  void testIncomparable() {
    // Not same color and none is Trumpf: D & E incomparable
    assertEquals(JassCardPartialOrder.INSTANCE.compare(D, E), OrderComparison.INCOMPARABLE);
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> JassCardPartialOrder.INSTANCE.compare(D, D));
  }
}
