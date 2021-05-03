// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTime;

/* package */ abstract class AbstractTwdDemo implements DemoInterface {
  @Override
  public final OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    TwdEntity twdEntity = configure(owlyAnimationFrame);
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          getRegion(), //
          TwdEntity.SHAPE, () -> twdEntity.getStateTimeNow().time());
      owlyAnimationFrame.addBackground(renderInterface);
    }
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlyAnimationFrame;
  }

  abstract TwdEntity configure(OwlyAnimationFrame owlyAnimationFrame);

  abstract Region<StateTime> getRegion();
}
