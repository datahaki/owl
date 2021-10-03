// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.java.win.OwlFrame;
import junit.framework.TestCase;

public class R2DemoTest extends TestCase {
  public void testSimple() throws InterruptedException {
    OwlFrame owlFrame = R2Demo.show();
    Thread.sleep(200);
    owlFrame.jFrame.dispose();
  }
}
