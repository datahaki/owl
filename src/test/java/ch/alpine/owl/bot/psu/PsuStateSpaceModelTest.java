// code by jph
package ch.alpine.owl.bot.psu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Reference;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.sophus.math.sample.BoxRandomSample;
import ch.alpine.sophus.math.sample.RandomSample;
import ch.alpine.sophus.math.sample.RandomSampleInterface;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.sca.Chop;

class PsuStateSpaceModelTest {
  @Test
  public void testNonTrivial() {
    Tensor u = Tensors.of(RationalScalar.HALF);
    Tensor x = Tensors.vector(1, 2);
    Scalar h = RationalScalar.of(1, 3);
    // Flow flow = StateSpaceModels.createFlow(, u);
    Tensor res = RungeKutta45Integrator.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    Tensor eul = EulerIntegrator.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    assertFalse(res.equals(eul));
  }

  @Test
  public void testReference() {
    Tensor u = Tensors.of(RationalScalar.HALF);
    Tensor x = Tensors.vector(1, 2);
    Scalar h = RationalScalar.of(1, 3);
    // Flow flow = StateSpaceModels.createFlow(, u);
    Tensor res = RungeKutta45Integrator.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    Tensor ref = RungeKutta45Reference.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    assertEquals(res, ref);
  }

  @Test
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

  @Test
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

  @Test
  public void testVectorField() {
    Tensor range = Tensors.vector(Math.PI, 3);
    RandomSampleInterface randomSampleInterface = BoxRandomSample.of(range.negate(), range);
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    new VectorFieldRender().setUV_Pairs(VectorFields.of(PsuStateSpaceModel.INSTANCE, points, Array.zeros(1), RealScalar.of(0.1)));
  }

  @Test
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
