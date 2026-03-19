// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.ani.api.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.util.ren.RegionRenderFactory;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.RegionConstraints;
import ch.alpine.owlets.glc.core.PlannerConstraint;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.chq.MemberQ;

public class Rice2dImageDemo implements DemoInterface {
  @Override
  public TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.of(-0.5);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    MemberQ region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new Rice2dEntity(mu, Tensors.vector(7, 6, 0, 0), trajectoryControl, controls);
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent, trajectoryEntity, plannerConstraint);
    owlAnimationFrame.addBackground(RegionRenderFactory.create(region));
    return owlAnimationFrame.timerFrame;
  }

  static void main() {
    new Rice2dImageDemo().runStandalone();
  }
}
