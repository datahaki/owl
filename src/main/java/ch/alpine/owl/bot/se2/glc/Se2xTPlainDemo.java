// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.region.Region;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Se2xTPlainDemo
// implements DemoInterface
{
  // @Override
  public void start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlyAnimationFrame.add(carxTEntity);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    carxTEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    Region<Tensor> region = r2ImageRegionWrap.region();
    Region<Tensor> se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), region);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(se2PointsVsRegion);
    MouseGoal.simple(owlyAnimationFrame, carxTEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    // ---
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    owlyAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    owlyAnimationFrame.jFrame.setVisible(true);
  }

  public static void main(String[] args) {
    new Se2xTPlainDemo().start();
  }
}
