// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.CoordinateBounds;

class LvEntityTest {
  @Test
  void testVectorField() {
    Tensor fallback_u = Array.zeros(1);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.of(1, 2);
    Collection<Tensor> controls = LvControls.create(2);
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator(stateSpaceModel, integrator, //
        new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    LvEntity lvEntity = new LvEntity(episodeIntegrator, trajectoryControl, stateSpaceModel, controls);
    lvEntity.delayHint();
    Tensor range = Tensors.vector(6, 5);
    // VectorFieldRender vectorFieldRender = ;
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(CoordinateBounds.of(Tensors.vector(0, 0), range));
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    new VectorFieldRender().setUV_Pairs(VectorFields.of(stateSpaceModel, points, fallback_u, RealScalar.of(0.04)));
  }
}
