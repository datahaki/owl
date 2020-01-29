// code by jph
package ch.ethz.idsc.owl.bot.psu;

import ch.ethz.idsc.owl.gui.ren.VectorFieldRender;
import ch.ethz.idsc.owl.math.VectorFields;
import ch.ethz.idsc.owl.math.flow.EulerIntegrator;
import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.flow.Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Integrator;
import ch.ethz.idsc.owl.math.flow.RungeKutta45Reference;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.owl.math.state.EpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.SimpleEpisodeIntegrator;
import ch.ethz.idsc.owl.math.state.StateTime;
import ch.ethz.idsc.sophus.math.sample.BoxRandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSample;
import ch.ethz.idsc.sophus.math.sample.RandomSampleInterface;
import ch.ethz.idsc.tensor.RationalScalar;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.alg.Array;
import ch.ethz.idsc.tensor.sca.Chop;
import junit.framework.TestCase;

public class PsuStateSpaceModelTest extends TestCase {
  public void testNonTrivial() {
    Tensor u = Tensors.of(RationalScalar.HALF);
    Tensor x = Tensors.vector(1, 2);
    Scalar h = RationalScalar.of(1, 3);
    Flow flow = StateSpaceModels.createFlow(PsuStateSpaceModel.INSTANCE, u);
    Tensor res = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    Tensor eul = EulerIntegrator.INSTANCE.step(flow, x, h);
    assertFalse(res.equals(eul));
  }

  public void testReference() {
    Tensor u = Tensors.of(RationalScalar.HALF);
    Tensor x = Tensors.vector(1, 2);
    Scalar h = RationalScalar.of(1, 3);
    Flow flow = StateSpaceModels.createFlow(PsuStateSpaceModel.INSTANCE, u);
    Tensor res = RungeKutta45Integrator.INSTANCE.step(flow, x, h);
    Tensor ref = RungeKutta45Reference.INSTANCE.step(flow, x, h);
    assertEquals(res, ref);
  }

  public void testSmall() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.00000001);
    episodeIntegrator.move(Tensors.vector(1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    Chop._04.requireClose(init.state(), stateTime.state());
  }

  public void testLarge() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.3);
    episodeIntegrator.move(Tensors.vector(1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    Chop._13.requireClose(stateTime.state(), Tensors.vector(1.6034722573306643, 2.015192617032934));
  }

  public void testVectorField() {
    Tensor range = Tensors.vector(Math.PI, 3);
    VectorFieldRender vectorFieldRender = new VectorFieldRender();
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(range.negate(), range);
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    vectorFieldRender.uv_pairs = //
        VectorFields.of(PsuStateSpaceModel.INSTANCE, points, Array.zeros(1), RealScalar.of(0.1));
  }

  public void testFail() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    try {
      episodeIntegrator.move(Tensors.vector(1), RealScalar.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }
}
