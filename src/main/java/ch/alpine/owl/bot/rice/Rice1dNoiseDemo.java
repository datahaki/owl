// code by jph
package ch.alpine.owl.bot.rice;

import java.util.Collection;

import ch.alpine.ascona.util.win.RenderInterface;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.glc.adapter.RegionConstraints;
import ch.alpine.owl.glc.core.PlannerConstraint;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.sophus.api.Region;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

public class Rice1dNoiseDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Scalar mu = RealScalar.ZERO;
    Collection<Tensor> controls = Rice2Controls.create1d(15);
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new Rice1dEntity(mu, Tensors.vector(0, 0), trajectoryControl, controls);
    owlAnimationFrame.add(trajectoryEntity);
    Region<Tensor> region = new R2NoiseRegion(RealScalar.of(0.5));
    PlannerConstraint plannerConstraint = RegionConstraints.timeInvariant(region);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, plannerConstraint);
    Tensor range = Tensors.vector(6, 1);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(CoordinateBounds.of(range.negate(), range));
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    RenderInterface renderInterface = new VectorFieldRender()
        .setUV_Pairs(VectorFields.of(Rice2StateSpaceModel.of(mu), points, Array.zeros(1), RealScalar.of(0.2)));
    owlAnimationFrame.addBackground(renderInterface);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new Rice1dNoiseDemo().start().jFrame.setVisible(true);
  }
}
