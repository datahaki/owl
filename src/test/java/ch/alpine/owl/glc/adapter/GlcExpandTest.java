// code by jph
package ch.alpine.owl.glc.adapter;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;

public class GlcExpandTest {
  @Test
  public void testFailNull() {
    AssertFail.of(() -> new GlcExpand(null));
  }
}
