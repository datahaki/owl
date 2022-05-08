// code by jph
package ch.alpine.owl.bot.se2.twd;

import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.glc.adapter.CatchyTrajectoryRegionQuery;
import ch.alpine.owl.glc.adapter.TrajectoryObstacleConstraint;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.owl.util.ren.RegionRenders;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.api.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class TwdNoiseDemo extends AbstractTwdDemo {
  private final TrajectoryRegionQuery trajectoryRegionQuery = //
      CatchyTrajectoryRegionQuery.timeInvariant(new R2NoiseRegion(RealScalar.of(0.1)));

  @Override // from AbstractTwdDemo
  TwdEntity configure(OwlAnimationFrame owlAnimationFrame) {
    TwdEntity twdEntity = TwdEntity.createDuckie(new StateTime(Tensors.vector(0, 0, 0), RealScalar.ZERO));
    owlAnimationFrame.add(twdEntity);
    PlannerConstraint plannerConstraint = new TrajectoryObstacleConstraint(trajectoryRegionQuery);
    MouseGoal.simple(owlAnimationFrame, twdEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenders.create(trajectoryRegionQuery));
    owlAnimationFrame.geometricComponent.setOffset(400, 400);
    return twdEntity;
  }

  @Override // from AbstractTwdDemo
  Region<StateTime> getRegion() {
    return trajectoryRegionQuery;
  }

  public static void main(String[] args) {
    new TwdNoiseDemo().start().jFrame.setVisible(true);
  }
}
