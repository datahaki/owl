// code by jph
package ch.alpine.sophus.gds;

import java.util.Arrays;

import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Dimensions;
import ch.alpine.tensor.alg.VectorQ;
import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class GeodesicDisplaysTest extends TestCase {
  public void testSimple() {
    assertTrue(12 <= ManifoldDisplays.ALL.size());
  }

  public void testToPoint() {
    for (ManifoldDisplay geodesicDisplay : ManifoldDisplays.ALL)
      try {
        Tensor xya = Tensors.vector(1, 2, 3);
        Tensor p = Serialization.copy(geodesicDisplay).project(xya);
        VectorQ.requireLength(geodesicDisplay.toPoint(p), 2);
        Tensor matrix = geodesicDisplay.matrixLift(p);
        assertEquals(Dimensions.of(matrix), Arrays.asList(3, 3));
      } catch (Exception exception) {
        System.out.println(geodesicDisplay);
        exception.printStackTrace();
        fail();
      }
  }
}
