// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2ImageRegionWrap;
import ch.alpine.owl.bot.r2.R2ImageRegions;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.bot.util.RegionRenders;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.MouseGoal;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Rice2dImageDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlyAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.of(-0.5);
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    R2ImageRegionWrap r2ImageRegionWrap = R2ImageRegions._GTOB;
    Region<Tensor> region = r2ImageRegionWrap.region();
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new Rice2dEntity(mu, Tensors.vector(7, 6, 0, 0), trajectoryControl, controls);
    owlyAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint);
    owlyAnimationFrame.addBackground(RegionRenders.create(region));
    owlyAnimationFrame.geometricComponent.setOffset(50, 700);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dImageDemo().start().jFrame.setVisible(true);
  }
}
