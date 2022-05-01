// code by jph
package ch.alpine.sophus.ext.dis;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;

class ManifoldDisplaysTest {
  @Test
  public void testSimple() {
    assertTrue(12 <= ManifoldDisplays.ALL.size());
  }

  @Test
  public void testToPoint() {
    for (ManifoldDisplay manifoldDisplay : ManifoldDisplays.ALL)
      try {
        Tensor xya = Tensors.vector(1, 2, 3);
        Tensor p = Serialization.copy(manifoldDisplay).project(xya);
        VectorQ.requireLength(manifoldDisplay.toPoint(p), 2);
        Tensor matrix = manifoldDisplay.matrixLift(p);
        assertEquals(Dimensions.of(matrix), Arrays.asList(3, 3));
      } catch (Exception exception) {
        System.out.println(manifoldDisplay);
        exception.printStackTrace();
        fail();
      }
  }
}
