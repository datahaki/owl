// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.java.ren.RenderInterface;
import ch.alpine.java.ren.VectorFieldRender;
import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

public class LvAnimationDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
    Collection<Tensor> controls = LvControls.create(2);
    Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator(stateSpaceModel, INTEGRATOR, //
        new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new LvEntity(episodeIntegrator, trajectoryControl, stateSpaceModel, controls);
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, EmptyPlannerConstraint.INSTANCE);
    // ---
    Tensor range = Tensors.vector(6, 5);
    // VectorFieldRender vectorFieldRender = ;
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(CoordinateBounds.of(Tensors.vector(0, 0), range));
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    RenderInterface renderInterface = new VectorFieldRender().setUV_Pairs( //
        VectorFields.of(stateSpaceModel, points, Array.zeros(1), RealScalar.of(0.04)));
    owlAnimationFrame.addBackground(renderInterface);
    // ---
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new LvAnimationDemo().start().jFrame.setVisible(true);
  }
}
