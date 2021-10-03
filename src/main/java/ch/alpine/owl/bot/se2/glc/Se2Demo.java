// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.bot.util.DemoInterface;

public abstract class Se2Demo implements DemoInterface {
  @Override
  public final OwlAnimationFrame start() {
    OwlAnimationFrame owlyAnimationFrame = new OwlAnimationFrame();
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    configure(owlyAnimationFrame);
    return owlyAnimationFrame;
  }

  protected abstract void configure(OwlAnimationFrame owlyAnimationFrame);
}
