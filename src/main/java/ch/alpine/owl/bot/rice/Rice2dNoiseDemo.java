// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.sophus.math.Region;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Rice2dNoiseDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = //
        new Rice2dEntity(mu, Tensors.vector(0, 0, 0, 0), trajectoryControl, controls);
    owlAnimationFrame.add(trajectoryEntity);
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.5));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, plannerConstraint);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice2dNoiseDemo().start().jFrame.setVisible(true);
  }
}
