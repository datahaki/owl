// code by jph
package ch.alpine.owl.math.state;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.owl.math.flow.EulerIntegrator;
import ch.alpine.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class BoundedEpisodeIntegratorTest extends TestCase {
  public void testSimple() {
    BoundedEpisodeIntegrator boundedEpisodeIntegrator = new BoundedEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1, 2), RealScalar.of(1)), //
        RealScalar.of(1));
    boundedEpisodeIntegrator.move(Tensors.vector(2, -1), RealScalar.of(3));
    StateTime stateTime = boundedEpisodeIntegrator.tail();
    assertEquals(stateTime.state(), Tensors.vector(5, 0));
    assertEquals(stateTime.time(), RealScalar.of(3));
    try {
      boundedEpisodeIntegrator.move(Tensors.vector(3, 4), RealScalar.of(3));
      fail();
    } catch (Exception exception) {
      // ---
    }
    try {
      boundedEpisodeIntegrator.move(Tensors.vector(3, 4), RealScalar.of(2.3));
      fail();
    } catch (Exception exception) {
      // ---
    }
  }

  public void testNegativeFail() {
    AssertFail.of(() -> new BoundedEpisodeIntegrator( //
        SingleIntegratorStateSpaceModel.INSTANCE, //
        EulerIntegrator.INSTANCE, //
        new StateTime(Tensors.vector(1, 2), RealScalar.ZERO), //
        RealScalar.of(-1)));
  }
}
