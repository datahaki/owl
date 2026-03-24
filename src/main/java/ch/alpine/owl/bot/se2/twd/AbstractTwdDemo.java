// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.api.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ abstract class AbstractTwdDemo implements DemoInterface {
  @Override
  public final TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    TwdEntity twdEntity = configure(owlAnimationFrame);
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          getRegion(), //
          TwdEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return twdEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.timerFrame.geometricComponent().getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
    owlAnimationFrame.timerFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame.timerFrame;
  }

  abstract TwdEntity configure(OwlAnimationFrame owlAnimationFrame);

  abstract Region<StateTime> getRegion();
}
