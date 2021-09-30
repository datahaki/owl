// code by jph
package ch.alpine.owl.bot.se2.rrts;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class Se2RrtsNodeCollectionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Se2RrtsNodeCollectionDemo());
  }
}
