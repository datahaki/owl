// code by jph
package ch.alpine.sophus.ext.dis;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.lie.se2.Se2Group;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.Tolerance;

class Se2DisplayTest {
  @Test
  public void testSimple() {
    assertEquals(Se2Display.INSTANCE.lieGroup(), Se2Group.INSTANCE);
  }

  @Test
  public void testProject() {
    Tensor tensor = Se2Display.INSTANCE.project(Tensors.vector(1, 2, Math.PI * 2));
    Tolerance.CHOP.requireZero(tensor.Get(2));
  }
}
