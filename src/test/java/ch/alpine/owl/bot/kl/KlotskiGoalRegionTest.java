// code by jph
package ch.alpine.owl.bot.kl;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensors;

class KlotskiGoalRegionTest {
  @Test
  public void testSimple() {
    KlotskiGoalRegion klotskiGoalRegion = new KlotskiGoalRegion(Tensors.vector(0, 4, 2));
    for (Huarong huarong : Huarong.values())
      assertFalse(klotskiGoalRegion.test(huarong.create().startState()));
  }
}
