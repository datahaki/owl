// code by jph
package ch.alpine.owl.bot.psu;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.VectorFields;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.util.ren.VectorFieldRender;
import ch.alpine.tensor.Rational;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.opt.nd.BoxRandomSample;
import ch.alpine.tensor.opt.nd.CoordinateBounds;
import ch.alpine.tensor.pdf.RandomSample;
import ch.alpine.tensor.pdf.RandomSampleInterface;
import ch.alpine.tensor.sca.Chop;

class PsuStateSpaceModelTest {
  @Test
  void testNonTrivial() {
    Tensor u = Tensors.of(Rational.HALF);
    Tensor x = Tensors.vector(1, 2);
    Scalar h = Rational.of(1, 3);
    // Flow flow = StateSpaceModels.createFlow(, u);
    Tensor res = RungeKutta45Integrator.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    Tensor eul = EulerIntegrator.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    assertFalse(res.equals(eul));
  }

  @Test
  void testReference() {
    Tensor u = Tensors.of(Rational.HALF);
    Tensor x = Tensors.vector(1, 2);
    Scalar h = Rational.of(1, 3);
    // Flow flow = StateSpaceModels.createFlow(, u);
    Tensor res = RungeKutta45Integrator.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    Tensor ref = RungeKutta45Reference.INSTANCE.step(PsuStateSpaceModel.INSTANCE, x, u, h);
    assertEquals(res, ref);
  }

  @Test
  void testSmall() {
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
  void testLarge() {
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
  void testVectorField() {
    Tensor range = Tensors.vector(Math.PI, 3);
    RandomSampleInterface randomSampleInterface = new BoxRandomSample(CoordinateBounds.of(range.negate(), range));
    Tensor points = RandomSample.of(randomSampleInterface, 1000);
    new VectorFieldRender().setUV_Pairs(VectorFields.of(PsuStateSpaceModel.INSTANCE, points, Array.zeros(1), RealScalar.of(0.1)));
  }

  @Test
  void testFail() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        PsuStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    assertThrows(Exception.class, () -> episodeIntegrator.move(Tensors.vector(1), RealScalar.of(3)));
  }
}
