// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.java.win.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ExamplePolygons;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.alpine.sophus.math.BijectionFamily;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Sin;

public class Se2xTPolygonDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlAnimationFrame.add(carxTEntity);
    // ---
    BijectionFamily shift = new SimpleR2TranslationFamily( //
        scalar -> Tensors.of(Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.2))), RealScalar.ZERO));
    Region<StateTime> region = new R2xTPolygonStateTimeRegion( //
        R2ExamplePolygons.CORNER_TOP_LEFT, shift, () -> carxTEntity.getStateTimeNow().time());
    PlannerConstraint plannerConstraint = RegionConstraints.stateTime(region);
    MouseGoal.simple(owlAnimationFrame, carxTEntity, plannerConstraint);
    // owlyAnimationFrame.addRegionRender(imageRegion);
    owlAnimationFrame.addBackground((RenderInterface) region);
    // ---
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Se2xTPolygonDemo().start().jFrame.setVisible(true);
  }
}
