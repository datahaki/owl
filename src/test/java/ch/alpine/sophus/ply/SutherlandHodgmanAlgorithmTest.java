// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.owl.math.AssertFail;
import ch.alpine.sophus.ply.SutherlandHodgmanAlgorithm.PolyclipResult;
import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.lie.r2.CirclePoints;
import ch.alpine.tensor.mat.HilbertMatrix;
import junit.framework.TestCase;

public class SutherlandHodgmanAlgorithmTest extends TestCase {
  public void testSingle() {
    Tensor clip = Array.zeros(1, 2);
    PolyclipResult polyclipResult = SutherlandHodgmanAlgorithm.of(clip).apply(CirclePoints.of(4));
    System.out.println(polyclipResult.tensor());
  }

  public void testSingle2() {
    Tensor clip = Array.zeros(1, 2);
    PolyclipResult polyclipResult = SutherlandHodgmanAlgorithm.of(CirclePoints.of(4)).apply(clip);
    System.out.println(polyclipResult.tensor());
  }

  public void testFail() {
    AssertFail.of(() -> SutherlandHodgmanAlgorithm.of(HilbertMatrix.of(2, 3)));
  }

  public void testLine() {
    Tensor tensor = SutherlandHodgmanAlgorithm.intersection( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(3, 3), //
        Tensors.vector(3, 2));
    assertEquals(tensor, Tensors.vector(3, 0));
    ExactTensorQ.require(tensor);
  }

  public void testSingular() {
    AssertFail.of(() -> SutherlandHodgmanAlgorithm.intersection( //
        Tensors.vector(1, 0), //
        Tensors.vector(2, 0), //
        Tensors.vector(4, 0), //
        Tensors.vector(9, 0)));
  }
}
