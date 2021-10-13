// code by jph
package ch.alpine.sophus.gds;

import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;
import junit.framework.TestCase;

public class Se2DisplayTest extends TestCase {
  public void testSimple() {
    assertEquals(Se2Display.INSTANCE.lieGroup(), Se2Group.INSTANCE);
  }

  public void testProject() {
    Tensor tensor = Se2Display.INSTANCE.project(Tensors.vector(1, 2, Math.PI * 2));
    Tolerance.CHOP.requireZero(tensor.Get(2));
  }
}
