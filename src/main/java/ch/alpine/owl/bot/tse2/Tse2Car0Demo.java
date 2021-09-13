// code by ynager
package ch.alpine.owl.bot.tse2;

import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.RenderInterface;
import ch.alpine.owl.gui.ren.MouseShapeRender;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.SimpleTrajectoryRegionQuery;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Tse2Car0Demo extends Tse2CarDemo {
  @Override
  void configure(OwlyAnimationFrame owlyAnimationFrame) {
    Tse2CarEntity tse2CarEntity = Tse2CarEntity.createDefault(new StateTime(Tensors.vector(6, 5, 1, 0), RealScalar.ZERO));
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = createConstraint(region);
    owlyAnimationFrame.add(tse2CarEntity);
    // LONGTERM add option to select goal velocity / range
    MouseGoal.simple(owlyAnimationFrame, tse2CarEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
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
          return owlyAnimationFrame.geometricComponent.getMouseSe2CState();
        }
      };
      owlyAnimationFrame.addBackground(renderInterface);
    }
  }

  public static void main(String[] args) {
    new Tse2Car0Demo().start().jFrame.setVisible(true);
  }
}
