// code by astoll
package ch.alpine.owl.demo.order;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.demo.order.JassCard.Farbe;
import ch.alpine.owl.demo.order.JassCard.Type;

class JassCardTest {
  @Test
  public void testIsTrumpf() {
    JassCard card = new JassCard(Farbe.EICHEL, Type.ACE, true);
    assertTrue(card.isTrumpf());
  }

  @Test
  public void testCheatChecker() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.SCHELLE, Type.ACE, true);
    JassCard card3 = new JassCard(Farbe.SCHELLE, Type.ACE, true);
    assertThrows(Exception.class, () -> card1.cheatChecker(card2));
    assertThrows(Exception.class, () -> card2.cheatChecker(card3));
  }

  @Test
  public void testIsLess() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.EICHEL, Type.JACK, true);
    JassCard card3 = new JassCard(Farbe.SCHELLE, Type.ACE, false);
    JassCard card4 = new JassCard(Farbe.EICHEL, Type.SEVEN, false);
    JassCard card5 = new JassCard(Farbe.SCHELLE, Type.ACE, false);
    assertFalse(card2.lessThan(card1));
    assertTrue(card1.lessThan(card2));
    assertTrue(card4.lessThan(card1));
    assertFalse(card1.lessThan(card3));
    assertFalse(card5.lessThan(card4));
    assertFalse(card4.lessThan(card5));
  }

  @Test
  public void testEquals() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card3 = new JassCard(Farbe.EICHEL, Type.JACK, true);
    assertTrue(card1.equals(card2));
    assertFalse(card2.equals(card3));
  }

  @Test
  public void testHashCode() {
    JassCard card1 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    JassCard card2 = new JassCard(Farbe.EICHEL, Type.ACE, true);
    assertTrue(card1.hashCode() == card2.hashCode());
    JassCard card3 = new JassCard(Farbe.EICHEL, Type.JACK, true);
    assertFalse(card1.hashCode() == card3.hashCode());
  }
}
