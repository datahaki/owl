// code by jph
package ch.ethz.idsc.sophus.app.api;

import ch.ethz.idsc.sophus.lie.se2.Se2Group;
import ch.ethz.idsc.tensor.Tensor;
import ch.ethz.idsc.tensor.Tensors;
import ch.ethz.idsc.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class Se2GeodesicDisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Se2GeodesicDisplay.INSTANCE.lieGroup(), Se2Group.INSTANCE);
  }

  public void testProject() {
    Tensor tensor = Se2GeodesicDisplay.INSTANCE.project(Tensors.vector(1, 2, Math.PI * 2));
    Tolerance.CHOP.requireZero(tensor.Get(2));
  }
}
