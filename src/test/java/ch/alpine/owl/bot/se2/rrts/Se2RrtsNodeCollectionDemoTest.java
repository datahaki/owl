// code by jph
package ch.alpine.owl.bot.se2.rrts;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class Se2RrtsNodeCollectionDemoTest {
  @Test
  void testSimple() {
    AbstractDemoHelper.offscreen(new Se2RrtsNodeCollectionDemo());
  }
}
