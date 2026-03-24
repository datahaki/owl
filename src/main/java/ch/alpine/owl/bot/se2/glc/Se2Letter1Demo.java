// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.util.ren.MouseShapeRender;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.owlets.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.reg.ImageRegion;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;

public class Se2Letter1Demo extends Se2CarDemo {
  @Override
  protected void configure(OwlAnimationFrame owlAnimationFrame) {
    CarEntity carEntity = CarEntity.createDefault(new StateTime(Tensors.vector(10, 5, 1), Quantity.of(0, "s")));
    ImageRegion imageRegion = R2ImageRegions.inside_0f5c();
    PlannerConstraint plannerConstraint = createConstraint(imageRegion);
    owlAnimationFrame.add(carEntity);
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent(), carEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(imageRegion));
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(imageRegion)), //
          CarEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return carEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlAnimationFrame.timerFrame.geometricComponent().getMouseSe2CState();
        }
      };
      owlAnimationFrame.addBackground(renderInterface);
    }
  }

  static void main() {
    new Se2Letter1Demo().runStandalone();
  }
}
