// code by jph
package ch.alpine.owl.bot.rn.rrts;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.util.win.OwlFrame;

class R2DemoTest {
  @Test
  void testSimple() throws InterruptedException {
    OwlFrame owlFrame = R2Demo.show();
    Thread.sleep(200);
    owlFrame.jFrame.dispose();
  }
}
