// code by jph
package ch.alpine.owl.glc.adapter;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class MultiConstraintAdapterTest extends TestCase {
  public void testFailNull() {
    AssertFail.of(() -> MultiConstraintAdapter.of(null));
  }
}
