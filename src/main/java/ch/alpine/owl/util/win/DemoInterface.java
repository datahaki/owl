// code by jph
package ch.alpine.owl.util.win;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ch.alpine.ascony.win.BaseFrame;
import ch.alpine.bridge.awt.AwtUtil;
import ch.alpine.bridge.awt.WindowBounds;
import ch.alpine.bridge.io.ResourceLocator;
import ch.alpine.bridge.lang.FriendlyFormat;
import ch.alpine.bridge.swing.LookAndFeels;

@FunctionalInterface
public interface DemoInterface {
  /** start demo
   * 
   * @return */
  BaseFrame getWindow();

  default void runStandalone() {
    ImageIO.setUseCache(false);
    LookAndFeels.autoDetect();
    BaseFrame baseFrame = getWindow();
    JFrame jFrame = baseFrame.jFrame;
    ResourceLocator resourceLocator = ResourceLocator.of(getClass());
    WindowBounds.persistent(jFrame, resourceLocator.properties(WindowBounds.class));
    jFrame.setTitle(FriendlyFormat.defaultTitle(getClass()));
    AwtUtil.ctrlW(jFrame);
    jFrame.setVisible(true);
  }
}
