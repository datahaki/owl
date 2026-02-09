// code by jph
package ch.alpine.owl.bot.se2.glc;

import java.util.Collection;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.Se2StateSpaceModel;
import ch.alpine.owl.math.flow.RungeKutta45Integrator;
import ch.alpine.owl.util.bot.FlowsInterface;
import ch.alpine.sophus.lie.se2.Se2CoveringGroup;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.num.Pi;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.qty.UnitSystem;
import ch.alpine.tensor.sca.Chop;

class Se2CarFlowsTest {
  @Test
  void testRadUnits() {
    Scalar speed = Quantity.of(2, "m*s^-1");
    Scalar rate_max = UnitSystem.SI().apply(Quantity.of(1, "rad*m^-1"));
    FlowsInterface carFlows = Se2CarFlows.standard(speed, rate_max);
    Collection<Tensor> collection = carFlows.getFlows(8);
    Tensor flow = collection.iterator().next();
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[rad]}").maps(UnitSystem.SI());
    Tensor r = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, Quantity.of(2, "s"));
    Chop._10.requireClose(r, //
        Tensors.fromString("{1.9786265584792444[m], 3.5241205617280174[m], -1}"));
  }

  @Test
  void testRadRadius() {
    Scalar speed = Quantity.of(1.423, "m*s^-1");
    Scalar rate = UnitSystem.SI().apply(Quantity.of(2.384, "rad*m^-1"));
    Tensor flow = Se2CarFlows.singleton(speed, rate);
    Tensor u = flow;
    Tensor origin = Tensors.fromString("{0[m], 0[m], 0}");
    Scalar half_turn = Pi.VALUE.divide(u.Get(2));
    Tensor res = Se2CoveringGroup.INSTANCE.spin(origin, u.multiply(half_turn));
    res = res.maps(Chop._12);
    Scalar radius = res.Get(1).divide(RealScalar.of(2));
    Chop._12.requireClose(radius.reciprocal(), rate);
  }

  @Test
  void testUnits() {
    Scalar speed = Quantity.of(2, "m*s^-1");
    Scalar rate_max = UnitSystem.SI().apply(Quantity.of(1, "m^-1"));
    FlowsInterface carFlows = Se2CarFlows.standard(speed, rate_max);
    Collection<Tensor> collection = carFlows.getFlows(8);
    Tensor flow = collection.iterator().next();
    Tensor x = Tensors.fromString("{1[m], 2[m], 3[]}").maps(UnitSystem.SI());
    Tensor r = RungeKutta45Integrator.INSTANCE.step(Se2StateSpaceModel.INSTANCE, x, flow, Quantity.of(2, "s"));
    Chop._10.requireClose(r, //
        Tensors.fromString("{1.9786265584792444[m], 3.5241205617280174[m], -1}"));
  }

  @Test
  void testRadius() {
    Scalar speed = Quantity.of(1.423, "m*s^-1");
    Scalar rate = UnitSystem.SI().apply(Quantity.of(2.384, "m^-1"));
    Tensor flow = Se2CarFlows.singleton(speed, rate);
    Tensor u = flow;
    Tensor origin = Tensors.fromString("{0[m], 0[m], 0}");
    Scalar half_turn = Pi.VALUE.divide(u.Get(2));
    Tensor res = Se2CoveringGroup.INSTANCE.spin(origin, u.multiply(half_turn));
    res = res.maps(Chop._12);
    Scalar radius = res.Get(1).divide(RealScalar.of(2));
    Chop._12.requireClose(radius.reciprocal(), rate);
  }
}
