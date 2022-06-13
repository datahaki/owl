// code by jph
package ch.alpine.owl.math.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.mat.HilbertMatrix;

class VectorLexicographicTest {
  @Test
  void testSimple1() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(3, 0, 1, 2));
    int c2 = Integer.compare(0, 3);
    assertEquals(c1, -1);
    assertEquals(c1, c2);
  }

  @Test
  void testSimple2() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 2));
    int c2 = Integer.compare(3, 2);
    assertEquals(c1, c2);
    assertEquals(c1, 1);
  }

  @Test
  void testSimple3() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 4));
    int c2 = Integer.compare(3, 4);
    assertEquals(c1, c2);
  }

  @Test
  void testSimple4() {
    int c1 = VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 1, 2, 3), Tensors.vector(0, 1, 2, 3));
    int c2 = Integer.compare(3, 3);
    assertEquals(c1, c2);
    assertEquals(c1, 0);
  }

  @Test
  void testLengthFail() {
    Tensor x = Tensors.vector(0, 1, 2, 3);
    Tensor y = Tensors.vector(0, 1, 2, 4, 2);
    assertThrows(Exception.class, () -> VectorLexicographic.COMPARATOR.compare(x, y));
    assertThrows(Exception.class, () -> VectorLexicographic.COMPARATOR.compare(y, x));
  }

  @Test
  void testMatrixFail() {
    assertThrows(Exception.class, () -> VectorLexicographic.COMPARATOR.compare(HilbertMatrix.of(3), HilbertMatrix.of(3)));
  }

  @Test
  void testScalarFail() {
    assertThrows(Exception.class, () -> VectorLexicographic.COMPARATOR.compare(RealScalar.ONE, RealScalar.ZERO));
  }

  @Test
  void testScalarVectorFail() {
    assertThrows(Exception.class, () -> VectorLexicographic.COMPARATOR.compare(RealScalar.ONE, Tensors.vector(0, 2, 3)));
    assertThrows(Exception.class, () -> VectorLexicographic.COMPARATOR.compare(Tensors.vector(0, 2, 3), RealScalar.ONE));
  }
}
