// code by jph
package ch.alpine.owl.math.state;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class TrajectorySampleTest extends TestCase {
  public void testHead() {
    TrajectorySample ts = TrajectorySample.head(new StateTime(Tensors.vector(2, 3), RealScalar.ZERO));
    assertFalse(ts.getFlow().isPresent());
    assertFalse(ts.toInfoString().isEmpty());
  }

  public void testFlow() {
    Tensor flow = Tensors.vector(1, 1);
    TrajectorySample ts = new TrajectorySample(new StateTime(Tensors.vector(2, 3), RealScalar.ZERO), flow);
    assertTrue(ts.getFlow().isPresent());
    assertFalse(ts.toInfoString().isEmpty());
  }
}
