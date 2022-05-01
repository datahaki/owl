// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class MultiConstraintAdapterTest {
  @Test
  public void testFailNull() {
    assertThrows(Exception.class, () -> MultiConstraintAdapter.of(null));
  }
}
