// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class Rice1dEmptyDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Tensor> controls = Rice2Controls.create1d(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = //
        new Rice1dEntity(mu, Tensors.vector(0, 0), trajectoryControl, controls);
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, EmptyPlannerConstraint.INSTANCE);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice1dEmptyDemo().start().jFrame.setVisible(true);
  }
}
