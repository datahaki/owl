// code by astoll
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class JassCardTest {
  @Test
  void testIsTrumpf() {
    JassCard card = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    assertTrue(card.isTrumpf());
  }

  @Test
  void testCheatChecker() {
    JassCard card1 = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    JassCard card2 = new JassCard(JassFarbe.SCHELLE, JassType.ACE, true);
    JassCard card3 = new JassCard(JassFarbe.SCHELLE, JassType.ACE, true);
    assertThrows(Exception.class, () -> card1.cheatChecker(card2));
    assertThrows(Exception.class, () -> card2.cheatChecker(card3));
  }

  @Test
  void testIsLess() {
    JassCard card1 = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    JassCard card2 = new JassCard(JassFarbe.EICHEL, JassType.JACK, true);
    JassCard card3 = new JassCard(JassFarbe.SCHELLE, JassType.ACE, false);
    JassCard card4 = new JassCard(JassFarbe.EICHEL, JassType.SEVEN, false);
    JassCard card5 = new JassCard(JassFarbe.SCHELLE, JassType.ACE, false);
    assertFalse(card2.lessThan(card1));
    assertTrue(card1.lessThan(card2));
    assertTrue(card4.lessThan(card1));
    assertFalse(card1.lessThan(card3));
    assertFalse(card5.lessThan(card4));
    assertFalse(card4.lessThan(card5));
  }

  @Test
  void testEquals() {
    JassCard card1 = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    JassCard card2 = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    JassCard card3 = new JassCard(JassFarbe.EICHEL, JassType.JACK, true);
    assertTrue(card1.equals(card2));
    assertFalse(card2.equals(card3));
  }

  @Test
  void testHashCode() {
    JassCard card1 = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    JassCard card2 = new JassCard(JassFarbe.EICHEL, JassType.ACE, true);
    assertTrue(card1.hashCode() == card2.hashCode());
    JassCard card3 = new JassCard(JassFarbe.EICHEL, JassType.JACK, true);
    assertFalse(card1.hashCode() == card3.hashCode());
  }
}
