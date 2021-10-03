// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.gui.MouseGoal;
import ch.alpine.owl.gui.ren.VectorFieldRender;
import ch.alpine.owl.math.VectorFields;
import ch.alpine.sophus.math.Region;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;

public class Rice1dNoiseDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlyAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Tensor> controls = Rice2Controls.create1d(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new Rice1dEntity(mu, Tensors.vector(0, 0), trajectoryControl, controls);
    owlyAnimationFrame.add(trajectoryEntity);
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.5));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, plannerConstraint);
    Tensor range = Tensors.vector(6, 1);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(range.negate(), range);
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    vectorFieldRender.uv_pairs = //
        VectorFields.of(Rice2StateSpaceModel.of(mu), points, Array.zeros(1), RealScalar.of(0.2));
    owlyAnimationFrame.addBackground(vectorFieldRender);
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice1dNoiseDemo().start().jFrame.setVisible(true);
  }
}
