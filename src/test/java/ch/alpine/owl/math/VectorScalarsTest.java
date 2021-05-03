// code by jph
package ch.alpine.owl.math;

import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class VectorScalarsTest extends TestCase {
  public void testVector() {
    Scalar scalar = VectorScalar.of(19, 2.5, 3);
    Tensor tensor = VectorScalars.vector(scalar);
    assertEquals(tensor, Tensors.vector(19, 2.5, 3));
  }

  public void testAt() {
    Scalar scalar = VectorScalar.of(19, 2.5, 3);
    assertEquals(VectorScalars.at(scalar, 0), RealScalar.of(19));
    assertEquals(VectorScalars.at(scalar, 1), RealScalar.of(2.5));
    assertEquals(VectorScalars.at(scalar, 2), RealScalar.of(3));
    assertTrue(ExactScalarQ.of(VectorScalars.at(scalar, 0)));
    assertFalse(ExactScalarQ.of(VectorScalars.at(scalar, 1)));
    assertTrue(ExactScalarQ.of(VectorScalars.at(scalar, 2)));
  }
}
