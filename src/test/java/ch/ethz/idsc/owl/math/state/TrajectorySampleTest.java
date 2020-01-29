// code by jph
package ch.ethz.idsc.owl.math.state;

import ch.ethz.idsc.owl.math.flow.Flow;
import ch.ethz.idsc.owl.math.model.SingleIntegratorStateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModel;
import ch.ethz.idsc.owl.math.model.StateSpaceModels;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.Tensors;
import junit.framework.TestCase;

public class TrajectorySampleTest extends TestCase {
  public void testHead() {
    TrajectorySample ts = TrajectorySample.head(new StateTime(Tensors.vector(2, 3), RealScalar.ZERO));
    assertFalse(ts.getFlow().isPresent());
    assertFalse(ts.toInfoString().isEmpty());
  }

  public void testFlow() {
    StateSpaceModel stateSpaceModel = SingleIntegratorStateSpaceModel.INSTANCE;
    Flow flow = StateSpaceModels.createFlow(stateSpaceModel, Tensors.vector(1, 1));
    TrajectorySample ts = new TrajectorySample(new StateTime(Tensors.vector(2, 3), RealScalar.ZERO), flow);
    assertTrue(ts.getFlow().isPresent());
    assertFalse(ts.toInfoString().isEmpty());
  }
}
