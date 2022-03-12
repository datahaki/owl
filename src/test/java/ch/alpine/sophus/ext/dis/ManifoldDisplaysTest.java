// code by jph
package ch.alpine.sophus.ext.dis;

import java.util.Arrays;

import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class ManifoldDisplaysTest extends TestCase {
  public void testSimple() {
    assertTrue(12 <= ManifoldDisplays.ALL.size());
  }

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
