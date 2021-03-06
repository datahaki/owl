// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.gui.ren.VectorFieldRender;
import ch.alpine.owl.gui.win.MouseGoal;
import ch.alpine.owl.gui.win.OwlyAnimationFrame;
import ch.alpine.owl.math.VectorFields;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
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

public class LvAnimationDemo implements DemoInterface {
  @Override
  public OwlyAnimationFrame start() {
    OwlyAnimationFrame owlyAnimationFrame = new OwlyAnimationFrame();
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
    Collection<Tensor> controls = LvControls.create(2);
    Integrator INTEGRATOR = RungeKutta45Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator(stateSpaceModel, INTEGRATOR, //
        new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new LvEntity(episodeIntegrator, trajectoryControl, stateSpaceModel, controls);
    owlyAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlyAnimationFrame, trajectoryEntity, EmptyPlannerConstraint.INSTANCE);
    // ---
    Tensor range = Tensors.vector(6, 5);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(Tensors.vector(0, 0), range);
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    vectorFieldRender.uv_pairs = //
        VectorFields.of(stateSpaceModel, points, Array.zeros(1), RealScalar.of(0.04));
    owlyAnimationFrame.addBackground(vectorFieldRender);
    // ---
    return owlyAnimationFrame;
  }

  public static void main(String[] args) {
    new LvAnimationDemo().start().jFrame.setVisible(true);
  }
}
