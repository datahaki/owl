// code by astoll
package ch.alpine.owl.order;

import java.io.Serializable;

/** Creates a card of the game jassen with the attributes color and card type
 * 
 * https://en.wikipedia.org/wiki/Jass */
/* package */ record JassCard(JassFarbe farbe, JassType type, boolean isTrumpf) implements Serializable {
  /** checks whether the two cards are exactly the same or if two different colors were assigned Trumpf
   * 
   * @param jassCard to check for eligibility
   * @throws RunTimeException */
  public void cheatChecker(JassCard jassCard) {
    if (equals(jassCard))
      throw new RuntimeException("card duplicate in deck");
    if (isTrumpf && //
        jassCard.isTrumpf && //
        !farbe.equals(jassCard.farbe))
      throw new RuntimeException("multi color trumpf");
  }

  /** checks whether the card is less (worse) than another card.
   * 
   * @param jassCard to be compared to
   * @return true if this card is less (according to the rules) than the given jassCard, false otherwise */
  public boolean lessThan(JassCard jassCard) {
    cheatChecker(jassCard);
    if (isTrumpf && jassCard.isTrumpf)
      if (type.trumpfOrdering < jassCard.type.trumpfOrdering)
        return true;
    if (!isTrumpf && jassCard.isTrumpf)
      return true;
    if (!isTrumpf && !jassCard.isTrumpf)
      if (farbe.equals(jassCard.farbe) && type.compareTo(jassCard.type) < 0)
        return true;
    return false;
  }
}
