// code by jph
package ch.alpine.owl.math.state;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class StateTimeTest {
  @Test
  public void testSimple() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    StateTime s2 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    assertEquals(s1, s2);
    assertEquals(s1.hashCode(), s2.hashCode());
  }

  @Test
  public void testNotEquals() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    StateTime s2 = new StateTime(Tensors.vector(1, 2, 1), RealScalar.of(2));
    StateTime s3 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(3));
    assertFalse(s1.equals(s2));
    assertFalse(s1.equals(s3));
  }

  @SuppressWarnings("unlikely-arg-type")
  @Test
  public void testEquals() {
    StateTime s1 = new StateTime(Tensors.vector(1, 0, 1), RealScalar.of(2));
    assertFalse(s1.equals(null));
    assertFalse(s1.equals(RealScalar.ONE));
  }

  @Test
  public void testFail() {
    assertThrows(Exception.class, () -> new StateTime(Tensors.vector(1, 2), null));
    assertThrows(Exception.class, () -> new StateTime(null, RealScalar.ZERO));
  }
}
