// code by ynager
package ch.alpine.owl.math;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.DoubleScalar;
import ch.alpine.tensor.ExactScalarQ;
import ch.alpine.tensor.MachineNumberQ;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Scalars;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.ext.Serialization;
import ch.alpine.tensor.qty.Quantity;
import ch.alpine.tensor.sca.Chop;
import ch.alpine.tensor.sca.Conjugate;
import ch.alpine.tensor.sca.Imag;
import ch.alpine.tensor.sca.Real;

public class VectorScalarTest {
  @Test
  public void testOne() {
    Scalar a = VectorScalar.of(1, -1, 2);
    assertEquals(a.add(VectorScalar.of(Tensors.vector(0, 1, 0))), VectorScalar.of(Tensors.vector(1, 0, 2)));
    assertEquals(a.divide(RealScalar.of(2)), VectorScalar.of(Tensors.vector(0.5, -0.5, 1)));
    assertEquals(a.zero(), VectorScalar.of(Tensors.vector(0, 0, 0)));
    assertEquals(((VectorScalar) a).vector().length(), 3);
    // ---
    a = VectorScalar.of(Tensors.vector(0.00001, 0.00005, 0));
    assertEquals(((VectorScalar) a).chop(Chop._04), VectorScalar.of(Tensors.vector(0, 0, 0)));
    // ---
    a = VectorScalar.of(Tensors.of(RealScalar.ONE, DoubleScalar.NEGATIVE_INFINITY));
    assertFalse(MachineNumberQ.of(a));
    assertFalse(ExactScalarQ.of(a));
    a = VectorScalar.of(Tensors.of(RealScalar.ONE, RealScalar.ONE));
    assertTrue(ExactScalarQ.of(a));
    // ---
    Scalar v1 = VectorScalar.of(Tensors.vector(1, 6, 1));
    Scalar v2 = VectorScalar.of(Tensors.vector(1, 5, 10));
    assertEquals(Scalars.compare(v1, v2), Integer.compare(1, 0));
  }

  @Test
  public void testComplex() {
    Scalar a = VectorScalar.of(Tensors.fromString("{1+3*I, 2-4*I}"));
    assertEquals(Real.of(a), VectorScalar.of(1, +2));
    assertEquals(Imag.of(a), VectorScalar.of(3, -4));
    assertEquals(Conjugate.of(a), VectorScalar.of(Tensors.fromString("{1-3*I, 2+4*I}")));
  }

  @Test
  public void testMultiply() {
    Scalar a = VectorScalar.of(1, 2, 3);
    Scalar b = VectorScalar.of(0, 3, 6);
    assertThrows(Exception.class, () -> a.multiply(b));
    Scalar c = Quantity.of(2, "Apples");
    Scalar d = a.multiply(c);
    assertEquals(d.toString(), "[2[Apples], 4[Apples], 6[Apples]]");
    // ---
    // the next expression gives [2, 4, 6][Apples]
    // which is not desired by cannot be prevented easily
    c.multiply(a);
  }

  @Test
  public void testChop() {
    Scalar a = VectorScalar.of(1, 2, 3);
    Scalar b = VectorScalar.of(1 + 1e-8, 2 - 1e-8, 3 + 2e-8);
    Chop._06.requireClose(a, b);
  }

  @Test
  public void testCommute() {
    Scalar a = VectorScalar.of(1, 2, 3);
    assertFalse(a.equals(null));
    Scalar b = RealScalar.of(4);
    assertEquals(a.multiply(b), b.multiply(a));
    assertFalse(a.equals(b));
    assertFalse(b.equals(a));
  }

  @Test
  public void testString() {
    Scalar a = VectorScalar.of(Tensors.vector(1, -1, 2));
    assertEquals(a.toString(), "[1, -1, 2]");
  }

  @Test
  public void testEmpty() {
    Scalar e1 = VectorScalar.of();
    Scalar e2 = VectorScalar.of(Tensors.empty());
    Scalar e3 = VectorScalar.of(Tensors.empty().stream().map(Scalar.class::cast));
    assertEquals(e1, e2);
    assertEquals(e1, e3);
  }

  @Test
  public void testSerializable() throws ClassNotFoundException, IOException {
    Scalar a = VectorScalar.of(1, 2, 3);
    Scalar b = Serialization.copy(a);
    assertEquals(a, b);
    assertEquals(b, a);
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> VectorScalar.of(Tensors.empty()).number());
    assertThrows(Exception.class, () -> VectorScalar.of(Tensors.empty().add(RealScalar.ONE)));
  }

  @Test
  public void testFailNested() {
    Scalar a = VectorScalar.of(Tensors.vector(1, -1, 2));
    assertThrows(Exception.class, () -> VectorScalar.of(Tensors.of(RealScalar.ONE, a)));
  }

  @Test
  public void testFailScalar() {
    assertThrows(Exception.class, () -> VectorScalar.of(RealScalar.ONE));
  }
}
