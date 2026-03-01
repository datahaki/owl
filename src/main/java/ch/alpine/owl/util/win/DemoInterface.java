// code by jph
package ch.alpine.owl.util.win;

import java.awt.Window;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.pro.WindowProvider;

@FunctionalInterface
public interface DemoInterface extends WindowProvider {
  /** start demo
   * 
   * @return */
  TimerFrame getTimerFrame();

  @Override
  default Window getWindow() {
    return getTimerFrame().jFrame;
  }
}
