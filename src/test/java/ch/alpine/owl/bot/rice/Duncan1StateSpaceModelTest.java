// code by jph
package ch.alpine.owl.bot.rice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.flow.MidpointIntegrator;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.owl.math.state.EpisodeIntegrator;
import ch.alpine.owl.math.state.FixedStateIntegrator;
import ch.alpine.owl.math.state.SimpleEpisodeIntegrator;
import ch.alpine.owl.math.state.StateIntegrator;
import ch.alpine.owl.math.state.StateTime;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;

class Duncan1StateSpaceModelTest {
  @Test
  void testScalar() {
    StateSpaceModel stateSpaceModel = new Duncan1StateSpaceModel(Quantity.of(0.1, "s^-1"));
    Tensor x = Tensors.fromString("{10[m*s^-1], 20[m*s^-1]}");
    Tensor u = Tensors.fromString("{-1[m*s^-2], -1[m*s^-2]}");
    Tensor fxu = stateSpaceModel.f(x, u).multiply(Quantity.of(1, "s"));
    assertEquals(fxu, Tensors.fromString("{-2[m*s^-1], -3[m*s^-1]}"));
  }

  @Test
  void testZero() {
    StateSpaceModel stateSpaceModel = new Duncan1StateSpaceModel(Quantity.of(0.0, "s^-1"));
    Tensor x = Tensors.fromString("{10[m*s^-1], 20[m*s^-1]}");
    Tensor u = Tensors.fromString("{-1[m*s^-2], -1[m*s^-2]}");
    Tensor fxu = stateSpaceModel.f(x, u).multiply(Quantity.of(1, "s"));
    assertEquals(fxu, Tensors.fromString("{-1[m*s^-1], -1[m*s^-1]}"));
  }

  @Test
  void testLimit() {
    Scalar lambda = Quantity.of(2.0, "s^-1");
    StateSpaceModel stateSpaceModel = new Duncan1StateSpaceModel(lambda);
    StateIntegrator stateIntegrator = FixedStateIntegrator.create( //
        RungeKutta45Integrator.INSTANCE, stateSpaceModel, Scalars.fromString("1/5[s]"), 99 * 5); // simulate for 100[s]
    StateTime stateTime = new StateTime(Tensors.of(Quantity.of(10, "m*s^-1")), Quantity.of(1, "s"));
    Scalar push = Quantity.of(3, "m*s^-2");
    List<StateTime> list = stateIntegrator.trajectory(stateTime, Tensors.of(push));
    StateTime last = list.getLast();
    assertEquals(last.time(), Quantity.of(100, "s"));
    Chop._12.requireClose(last.state().get(0), push.divide(lambda));
  }

  @Test
  void testSimple() {
    Tensor speed = Tensors.fromString("{10[m*s^-1]}");
    EpisodeIntegrator episodeIntegrator = new SimpleEpisodeIntegrator( //
        new Duncan1StateSpaceModel(Quantity.of(0.2, "s^-1")), //
        MidpointIntegrator.INSTANCE, //
        new StateTime(speed, Quantity.of(0, "s")));
    Tensor accel = Tensors.of(Quantity.of(3, "m*s^-2"));
    episodeIntegrator.move(accel, Quantity.of(1, "s"));
    StateTime stateTime = episodeIntegrator.tail();
    assertTrue(Scalars.lessThan(speed.Get(0), stateTime.state().Get(0)));
  }

  @Test
  void testFail() {
    assertThrows(Exception.class, () -> new Duncan1StateSpaceModel(Quantity.of(-1.0, "s^-1")));
  }
}
