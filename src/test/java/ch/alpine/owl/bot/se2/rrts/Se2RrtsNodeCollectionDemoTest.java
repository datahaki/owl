// code by jph
package ch.alpine.owl.bot.se2.rrts;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.ascona.util.win.AbstractDemoHelper;

class Se2RrtsNodeCollectionDemoTest {
  @Test
  void testSimple() {
    AbstractDemoHelper abstractDemoHelper = AbstractDemoHelper.offscreen(new Se2RrtsNodeCollectionDemo());
    assertEquals(abstractDemoHelper.errors(), 0);
  }
}
