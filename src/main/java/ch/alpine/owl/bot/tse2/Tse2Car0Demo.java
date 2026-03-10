// code by ynager
package ch.alpine.owl.bot.tse2;

import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;
import ch.alpine.tensor.qty.Quantity;

public class Tse2Car0Demo extends Tse2CarDemo {
  @Override
  void configure(OwlAnimationFrame owlAnimationFrame) {
    Tse2CarEntity tse2CarEntity = Tse2CarEntity.createDefault(new StateTime(Tensors.vector(6, 5, 1, 0), Quantity.of(0, "s")));
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    MemberQ region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = createConstraint(region);
    owlAnimationFrame.add(tse2CarEntity);
    // TODO OWL API add option to select goal velocity / range
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent, tse2CarEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(region));
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(region)), //
          Tse2CarEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return tse2CarEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.timerFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
  }

  static void main() {
    new Tse2Car0Demo().runStandalone();
  }
}
