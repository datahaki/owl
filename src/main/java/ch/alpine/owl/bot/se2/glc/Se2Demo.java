// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;

public abstract class Se2Demo implements DemoInterface {
  @Override
  public final OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setTitle(getClass().getSimpleName());
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    configure(owlAnimationFrame);
    return owlAnimationFrame;
  }

  protected abstract void configure(OwlAnimationFrame owlAnimationFrame);
}
