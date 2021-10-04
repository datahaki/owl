// code by jph
package ch.alpine.owl.bot.psu;

import ch.alpine.java.win.OwlAnimationFrame;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.bot.util.DemoInterface;
import ch.alpine.owl.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owl.gui.ren.AxesRender;
import ch.alpine.owl.gui.ren.VectorFieldRender;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
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

public class PsuAnimationDemo implements DemoInterface {
  @Override
  public OwlAnimationFrame start() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        new StateTime(Tensors.vector(0, 0), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new PsuTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new PsuEntity(episodeIntegrator, trajectoryControl);
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame, trajectoryEntity, EmptyPlannerConstraint.INSTANCE);
    // ---
    Tensor range = Tensors.vector(Math.PI, 3);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(range.negate(), range);
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    vectorFieldRender.uv_pairs = //
        VectorFields.of(PsuStateSpaceModel.INSTANCE, points, Array.zeros(1), RealScalar.of(0.1));
    owlAnimationFrame.addBackground(vectorFieldRender);
    owlAnimationFrame.addBackground(AxesRender.INSTANCE);
    return owlAnimationFrame;
  }

  public static void main(String[] args) {
    new PsuAnimationDemo().start().jFrame.setVisible(true);
  }
}
