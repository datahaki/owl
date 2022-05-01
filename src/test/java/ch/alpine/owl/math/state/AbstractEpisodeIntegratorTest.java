// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.rice.Duncan1StateSpaceModel;
import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.flow.Integrator;
import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.flow.RungeKutta4Integrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class AbstractEpisodeIntegratorTest {
  @Test
  public void testSmall() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.00000001);
    episodeIntegrator.move(Tensors.vector(1, 1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    Chop._04.requireClose(init.state(), stateTime.state());
  }

  @Test
  public void testLarge() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2, 3), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        Se2StateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    Scalar now = RealScalar.of(3.3);
    episodeIntegrator.move(Tensors.vector(1, 0, 1), now);
    StateTime stateTime = episodeIntegrator.tail();
    assertEquals(stateTime.time(), now);
    assertFalse(init.equals(stateTime));
    Chop._13.requireClose(stateTime.state(), Tensors.vector(0.7011342979097925, 1.9974872733093685, 3.3));
  }

  @Test
  public void testFail() {
    Integrator integrator = RungeKutta45Integrator.INSTANCE;
    StateTime init = new StateTime(Tensors.vector(1, 2), RealScalar.of(3));
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        Se2StateSpaceModel.INSTANCE, integrator, //
        init);
    assertEquals(episodeIntegrator.tail(), init);
    assertThrows(Exception.class, () -> episodeIntegrator.move(Tensors.vector(1), RealScalar.of(3)));
  }

  @Test
  public void testRice1Units() {
    StateSpaceModel stateSpaceModel = new Duncan1StateSpaceModel(Quantity.of(3, "s^-1"));
    Tensor x = Tensors.fromString("{1[m*s^-1], 2[m*s^-1]}");
    Tensor u = Tensors.fromString("{5[m*s^-2], -2[m*s^-2]}");
    Scalar t = Scalars.fromString("3[s]");
    Scalar p = Scalars.fromString("2[s]");
    Integrator[] ints = new Integrator[] { //
        EulerIntegrator.INSTANCE, //
        MidpointIntegrator.INSTANCE, //
        RungeKutta4Integrator.INSTANCE, //
        RungeKutta45Integrator.INSTANCE //
    };
    for (Integrator integrator : ints) {
      AbstractEpisodeIntegrator aei = new SimpleEpisodeIntegrator( //
          stateSpaceModel, //
          integrator, new StateTime(x, t));
      Tensor flow = u;
      List<StateTime> list = aei.abstract_move(flow, p);
      assertEquals(list.size(), 1);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }
}
