// code by jph
package ch.alpine.owl.bot.se2.glc;

import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.se2.Se2PointsVsRegions;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

public class Se2xTPlainDemo
// implements DemoInterface
{
  // @Override
  public void start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    CarxTEntity carxTEntity = new CarxTEntity(new StateTime(Tensors.vector(6.75, 5.4, 1 + Math.PI), RealScalar.ZERO));
    owlAnimationFrame.add(carxTEntity);
    // ---
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    carxTEntity.extraCosts.add(r2ImageRegionWrap.costFunction());
    MemberQ region = r2ImageRegionWrap.region();
    MemberQ se2PointsVsRegion = Se2PointsVsRegions.line(Tensors.vector(0.2, 0.1, 0, -0.1), region);
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(se2PointsVsRegion);
    MouseGoal.simple(owlAnimationFrame, carxTEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(region));
    // ---
    owlAnimationFrame.geometricComponent.setOffset(50, 700);
    owlAnimationFrame.jFrame.setBounds(100, 50, 1200, 800);
    owlAnimationFrame.jFrame.setVisible(true);
  }

  static void main() {
    new Se2xTPlainDemo().start();
  }
}
