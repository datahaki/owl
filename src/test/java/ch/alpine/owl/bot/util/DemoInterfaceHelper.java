// code by jph
package ch.alpine.owl.bot.util;

import ch.alpine.ascona.util.win.BaseFrame;
import ch.alpine.owl.util.win.DemoInterface;

public enum DemoInterfaceHelper {
  ;
  public static void brief(DemoInterface demoInterface, long millis) {
    BaseFrame baseFrame = demoInterface.start();
    baseFrame.jFrame.setTitle(demoInterface.getClass().getSimpleName());
    baseFrame.jFrame.setVisible(true);
    try {
      Thread.sleep(millis);
    } catch (Exception exception) {
      // ---
    }
    baseFrame.jFrame.setVisible(false);
  }

  public static void brief(DemoInterface demoInterface) {
    brief(demoInterface, 400);
  }
}
