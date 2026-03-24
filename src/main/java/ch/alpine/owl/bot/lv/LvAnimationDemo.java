// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import ch.alpine.ascony.win.TimerFrame;
import ch.alpine.bridge.gfx.RenderInterface;
import ch.alpine.owl.ani.api.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.MouseGoal;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryEntity;
import ch.alpine.owl.util.bot.VectorFields;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.owl.util.win.DemoInterface;
import ch.alpine.owl.util.win.OwlAnimationFrame;
import ch.alpine.owlets.glc.adapter.EmptyPlannerConstraint;
import ch.alpine.owlets.math.state.EpisodeIntegrator;
import ch.alpine.owlets.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owlets.math.state.StateTime;
import ch.alpine.sophis.flow.StateSpaceModel;
import ch.alpine.sophis.flow.TimeIntegrator;
import ch.alpine.sophis.flow.TimeIntegrators;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.qty.Quantity;

public class LvAnimationDemo implements DemoInterface {
  @Override
  public TimerFrame getWindow() {
    OwlAnimationFrame owlAnimationFrame = new OwlAnimationFrame();
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.EXAMPLE;
    Collection<Tensor> controls = LvControls.create(Quantity.of(1.0, "s^-1"), 2);
    TimeIntegrator INTEGRATOR = TimeIntegrators.RK45;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator(stateSpaceModel, INTEGRATOR, //
        new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    TrajectoryEntity trajectoryEntity = new LvEntity(episodeIntegrator, trajectoryControl, stateSpaceModel, controls);
    owlAnimationFrame.add(trajectoryEntity);
    MouseGoal.simple(owlAnimationFrame.timerFrame.geometricComponent(), trajectoryEntity, EmptyPlannerConstraint.INSTANCE);
    // ---
    Tensor range = Tensors.vector(6, 5);
    // VectorFieldRender vectorFieldRender = ;
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of(Tensors.vector(0, 0), range));
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    RenderInterface renderInterface = new VectorFieldRender().setUV_Pairs( //
        VectorFields.of(stateSpaceModel, points, Array.zeros(1), RealScalar.of(0.04)));
    owlAnimationFrame.addBackground(renderInterface);
    // ---
    return owlAnimationFrame.timerFrame;
  }

  static void main() {
    new LvAnimationDemo().runStandalone();
  }
}
