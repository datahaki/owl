// code by jph
package ch.alpine.sophus.ply;

import ch.alpine.tensor.ExactTensorQ;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.alg.Array;
import ch.alpine.tensor.red.Mean;
import ch.alpine.tensor.sca.Sign;
import junit.framework.TestCase;

public class ArrowheadTest extends TestCase {
  public void testOriented() {
    Tensor polygon = Arrowhead.of(1);
    Scalar scalar = PolygonArea.of(polygon);
    assertEquals(scalar, RationalScalar.HALF);
    Sign.requirePositive(scalar);
    Tensor centroid = PolygonCentroid.of(polygon);
    ExactTensorQ.require(centroid);
    assertEquals(centroid, Array.zeros(2));
  }

  public void testExact() {
    ExactTensorQ.require(Arrowhead.of(RealScalar.ONE));
  }

  public void testLength() {
    assertEquals(Arrowhead.of(6).length(), 3);
  }

  public void testMean() {
    Tensor tensor = Mean.of(Arrowhead.of(2));
    assertEquals(tensor, tensor.map(Scalar::zero));
  }
}
