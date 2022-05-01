// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class GlcExpandTest {
  @Test
  public void testFailNull() {
    assertThrows(Exception.class, () -> new GlcExpand(null));
  }
}
