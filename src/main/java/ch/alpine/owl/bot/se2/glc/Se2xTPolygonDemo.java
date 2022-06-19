// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2xTPolygonStateTimeRegion;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.hs.r2.SimpleR2TranslationFamily;
import ch.alpine.sophus.math.api.BijectionFamily;
import ch.alpine.sophus.math.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.tri.Sin;

public class Se2xTPolygonDemo implements DemoInterface {
  private static final Tensor CORNER_TOP_LEFT = Tensors.matrix(new Number[][] { //
      { 3, 0 }, //
      { 4, 0 }, //
      { 4, 4 }, //
      { 1, 4 }, //
      { 1, 3 }, //
      { 3, 3 } //
  });

  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlAnimationFrame.add(carxTEntity);
    // ---
    BijectionFamily shift = new SimpleR2TranslationFamily( //
        scalar -> Tensors.of(Sin.FUNCTION.apply(scalar.multiply(RealScalar.of(0.2))), RealScalar.ZERO));
    Region<StateTime> region = new R2xTPolygonStateTimeRegion( //
        CORNER_TOP_LEFT, shift, () -> carxTEntity.getStateTimeNow().time());
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
