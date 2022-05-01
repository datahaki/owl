// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.win.DemoInterface;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;

/* package */ abstract class AbstractTwdDemo implements DemoInterface {
  @Override
  public final OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
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
          return owlAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  abstract TwdEntity configure(OwlAnimationFrame owlAnimationFrame);

  abstract Region<StateTime> getRegion();
}
