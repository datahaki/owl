// code by jph
package ch.alpine.owl.glc.adapter;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import ch.alpine.owlets.glc.adapter.GlcExpand;

class GlcExpandTest {
  @Test
  void testFailNull() {
    assertThrows(Exception.class, () -> new GlcExpand(null));
  }
}
