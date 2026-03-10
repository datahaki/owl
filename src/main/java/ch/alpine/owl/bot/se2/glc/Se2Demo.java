// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.lang.FriendlyFormat;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;

public abstract class Se2Demo implements DemoInterface {
  @Override
  public final TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    // owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.timerFrame.setTitle(FriendlyFormat.defaultTitle(getClass()));
    owlAnimationFrame.timerFrame.setBounds(100, 50, 1200, 800);
    configure(owlAnimationFrame);
    return owlAnimationFrame.timerFrame;
  }

  protected abstract void configure(OwlAnimationFrame owlAnimationFrame);
}
