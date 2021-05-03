// code by jph
package ch.alpine.owl.math.state;

import java.util.List;

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
import junit.framework.TestCase;

public class SimpleEpisodeIntegratorTest extends TestCase {
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor x = Tensors.vector(1, 2);
    Tensor u = Tensors.vector(5, -2);
    Scalar t = RealScalar.of(3);
    Scalar p = RealScalar.of(2);
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
      Tensor cmp = x.add(u.multiply(p));
      assertEquals(list.get(0).state(), cmp);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }

  public void testUnits1() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor x = Tensors.fromString("{1[m], 2[m]}");
    Tensor u = Tensors.fromString("{5[m], -2[m]}");
    Scalar t = RealScalar.of(3);
    Scalar p = RealScalar.of(2);
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
      Tensor cmp = x.add(u.multiply(p));
      assertEquals(list.get(0).state(), cmp);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }

  public void testUnits2() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Tensor x = Tensors.fromString("{1[m], 2[m]}");
    Tensor u = Tensors.fromString("{5[m*s^-1], -2[m*s^-1]}");
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
      Tensor cmp = x.add(u.multiply(p));
      assertEquals(list.get(0).state(), cmp);
      assertEquals(list.get(0).time(), t.add(p));
    }
  }
}
