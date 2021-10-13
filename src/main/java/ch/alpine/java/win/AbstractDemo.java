// code by jph
package ch.alpine.java.win;

import ch.alpine.java.ren.RenderInterface;

public abstract class AbstractDemo implements RenderInterface {
  public final TimerFrame timerFrame = new TimerFrame();

  public AbstractDemo() {
    timerFrame.jFrame.setTitle(getClass().getSimpleName());
    timerFrame.geometricComponent.addRenderInterface(this);
  }

  /** @param width
   * @param height */
  public final void setVisible(int width, int height) {
    timerFrame.jFrame.setBounds(100, 100, width, height);
    timerFrame.jFrame.setVisible(true);
  }
}
