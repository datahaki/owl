// code by jph
package ch.alpine.sophus.ext.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

public class ArgMinValueTest {
  @Test
  public void testSimple() {
    ArgMinValue argMinValue = ArgMinValue.of(Tensors.vector(3, 2, 3, 4, 5, 1, 2, 3, 4));
    assertEquals(argMinValue.index(), 5);
    assertFalse(argMinValue.index(RationalScalar.HALF).isPresent());
    assertEquals(argMinValue.index(RealScalar.of(123)).get(), (Integer) 5);
    assertEquals(argMinValue.value().get(), RealScalar.ONE);
    assertFalse(argMinValue.value(RealScalar.of(0.1)).isPresent());
    assertEquals(argMinValue.value(RealScalar.of(123)).get(), RealScalar.ONE);
  }
}
