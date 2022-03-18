// code by jph
package ch.alpine.owl.bot.se2;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.glc.Se2CarFlows;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.Chop;

public class Se2CarIntegratorTest {
  @Test
  public void testStraight() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(2);
    Tensor flow = Se2CarFlows.singleton(RealScalar.ONE, RealScalar.ZERO);
    Se2StateSpaceModel.INSTANCE.f(x, flow);
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Chop._10.requireClose(impl, expl);
  }

  @Test
  public void testRotate1() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(0.25);
    Tensor flow = Se2CarFlows.singleton(RealScalar.ONE, RealScalar.ONE);
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Chop._10.requireClose(impl, expl);
  }

  @Test
  public void testRotate2() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(0.25);
    Tensor flow = Se2CarFlows.singleton(RealScalar.of(0.5), RealScalar.of(2));
    Se2StateSpaceModel.INSTANCE.f(x, flow);
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Tensor imp1 = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Chop._07.requireClose(imp1, expl);
  }

  @Test
  public void testRotateHN() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(-.25);
    Tensor flow = Se2CarFlows.singleton(RealScalar.of(0.7), RealScalar.of(1.2));
    Se2StateSpaceModel.INSTANCE.f(x, flow);
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Chop._07.requireClose(impl, expl);
  }

  @Test
  public void testRotateUN() {
    Tensor x = Tensors.vector(-1, -2, 1);
    Scalar h = RealScalar.of(0.25);
    Tensor flow = Se2CarFlows.singleton(RealScalar.of(-0.8), RealScalar.of(2));
    Tensor expl = Se2CarIntegrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Tensor impl = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, h);
    Chop._07.requireClose(impl, expl);
  }
}
