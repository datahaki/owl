// code by jph
package ch.alpine.owl.util.win;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.pro.WindowProvider;

@FunctionalInterface
public interface DemoInterface extends WindowProvider {
  /** start demo
   * 
   * @return */
  @Override
  TimerFrame getWindow();
}
