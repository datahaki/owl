// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.gui.win.OwlyFrame;
import junit.framework.TestCase;

public class R2DemoTest extends TestCase {
  public void testSimple() throws InterruptedException {
    OwlyFrame owlyFrame = R2Demo.show();
    Thread.sleep(200);
    owlyFrame.jFrame.dispose();
  }
}
