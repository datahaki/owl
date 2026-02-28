// code by jph
package ch.alpine.owl.util.win;

import java.awt.Window;

import ch.alpine.ascony.win.BaseFrame;
import ch.alpine.bridge.pro.WindowProvider;

@FunctionalInterface
public interface DemoInterface extends WindowProvider {
  /** start demo
   * 
   * @return */
  BaseFrame getBaseFrame();

  @Override
  default Window getWindow() {
    return getBaseFrame().jFrame;
  }
}
