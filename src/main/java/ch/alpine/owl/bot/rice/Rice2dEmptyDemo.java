// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.owl.ani.api.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Rice2dEmptyDemo implements DemoInterface {
  @Override
  public TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Tensor> controls = Rice2Controls.create2d(1).getFlows(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new Rice2dEntity(mu, Tensors.vector(0, 0, 0, 0), trajectoryControl, controls);
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent(), trajectoryEntity, EmptyPlannerConstraint.INSTANCE);
    return owlAnimationFrame.timerFrame;
  }

  static void main() {
    new Rice2dEmptyDemo().runStandalone();
  }
}
