// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.MouseGoal;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Se2Letter2Demo extends Se2CarDemo {
  @Override
  protected void configure(OwlAnimationFrame owlyAnimationFrame) {
    CarEntity carEntity = CarEntity.createDefault(new StateTime(Tensors.vector(6, 5, 1), RealScalar.ZERO));
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = createConstraint(region);
    owlyAnimationFrame.add(carEntity);
    MouseGoal.simple(owlyAnimationFrame, carEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    {
      RenderInterface renderInterface = new MouseShapeRender( //
          SimpleTrajectoryRegionQuery.timeInvariant(line(region)), //
          CarEntity.SHAPE) {
        @Override
        public Scalar getTime() {
          return carEntity.getStateTimeNow().time();
        }

        @Override
        public Tensor getSe2() {
          return owlyAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Se2Letter2Demo().start().jFrame.setVisible(true);
  }
}
