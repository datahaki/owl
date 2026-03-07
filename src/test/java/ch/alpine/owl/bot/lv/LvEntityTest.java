// code by jph
package ch.alpine.owl.bot.lv;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.ani.adapter.EuclideanTrajectoryControl;
import ch.alpine.owl.ani.api.TrajectoryControl;
import ch.alpine.owl.util.bot.VectorFields;
import ch.alpine.owl.util.ren.VectorFieldRender;
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

class LvEntityTest {
  @Test
  void testVectorField() {
    Tensor fallback_u = Array.zeros(1);
    StateSpaceModel stateSpaceModel = LvStateSpaceModel.EXAMPLE;
    Collection<Tensor> controls = LvControls.create(Quantity.of(1.0, "s^-1"), 2);
    TimeIntegrator integrator = TimeIntegrators.RK45;
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator(stateSpaceModel, integrator, //
        new StateTime(Tensors.vector(2, 0.3), RealScalar.ZERO));
    TrajectoryControl trajectoryControl = new EuclideanTrajectoryControl();
    LvEntity lvEntity = new LvEntity(episodeIntegrator, trajectoryControl, stateSpaceModel, controls);
    lvEntity.delayHint();
    Tensor range = Tensors.vector(6, 5);
    // VectorFieldRender vectorFieldRender = ;
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of(Tensors.vector(0, 0), range));
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    new VectorFieldRender().setUV_Pairs(VectorFields.of(stateSpaceModel, points, fallback_u, RealScalar.of(0.04)));
  }
}
