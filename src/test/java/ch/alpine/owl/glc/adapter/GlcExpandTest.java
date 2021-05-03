// code by jph
package ch.alpine.owl.glc.adapter;

import ch.alpine.owl.math.AssertFail;
import junit.framework.TestCase;

public class GlcExpandTest extends TestCase {
  public void testFailNull() {
    AssertFail.of(() -> new GlcExpand(null));
  }
}
