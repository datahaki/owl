// code by jph
package ch.alpine.owl.util.win;

import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArrayList;

import ch.alpine.ascony.win.AbstractDemo;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.api.AnimationInterface;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.qty.Timing;

public class OwlAnimationDemo extends AbstractDemo {
  private final List<AnimationInterface> animationInterfaces = new CopyOnWriteArrayList<>();

  public OwlAnimationDemo() {
    // periodic task for integration
    timerFrame.timer_schedule(new TimerTask() {
      final Timing timing = Timing.started();

      @Override
      public void run() {
        Scalar now = timing.seconds();
        animationInterfaces.forEach(animationInterface -> animationInterface.integrate(now));
      }
    }, 100, 20);
  }

  public void add(AnimationInterface animationInterface) {
    animationInterfaces.add(animationInterface);
    if (animationInterface instanceof RenderInterface renderInterface)
      geometricComponent().addRenderInterface(renderInterface);
  }
}
