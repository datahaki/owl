// code by jph
package ch.alpine.owl.glc.adapter;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.AssertFail;

public class MultiConstraintAdapterTest {
  @Test
  public void testFailNull() {
    AssertFail.of(() -> MultiConstraintAdapter.of(null));
  }
}
