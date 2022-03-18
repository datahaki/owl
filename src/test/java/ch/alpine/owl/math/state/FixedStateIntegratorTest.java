// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.bot.se2.Se2CarIntegrator;
import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.owl.math.model.StateSpaceModel;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;

public class FixedStateIntegratorTest {
  @Test
  public void testSimple() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    FixedStateIntegrator fsi = //
        FixedStateIntegrator.create(EulerIntegrator.INSTANCE, stateSpaceModel, RationalScalar.of(1, 2), 3);
    Tensor u = Tensors.vector(1, 2);
    // Flow flow = StateSpaceModels.createFlow(SingleIntegratorStateSpaceModel.INSTANCE, );
    List<StateTime> list = fsi.trajectory(new StateTime(Tensors.vector(2, 3), RealScalar.of(10)), u);
    assertEquals(list.size(), 3);
    assertEquals(list.get(2).time(), Scalars.fromString("10+3/2"));
    assertEquals(fsi.getTimeStepTrajectory(), RationalScalar.of(3, 2));
  }

  @Test
  public void testCarEx() {
    Scalar dt = RationalScalar.of(1, 10);
    FixedStateIntegrator FIXEDSTATEINTEGRATOR = //
        FixedStateIntegrator.create(Se2CarIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, dt, 4);
    Scalar r = FIXEDSTATEINTEGRATOR.getTimeStepTrajectory();
    assertEquals(r, dt.multiply(RealScalar.of(4)));
  }

  @Test
  public void testFail1() {
    AssertFail.of(() -> FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RealScalar.of(-.1), 3));
  }

  @Test
  public void testFail2() {
    AssertFail.of(() -> FixedStateIntegrator.create(EulerIntegrator.INSTANCE, SingleIntegratorStateSpaceModel.INSTANCE, RealScalar.of(0), 3));
  }
}
