// code by jph
package ch.alpine.owl.math.region;

import java.io.IOException;

import ch.alpine.tensor.ext.Serialization;
import junit.framework.TestCase;

public class RegionsTest extends TestCase {
  public void testSimple() {
    assertTrue(Regions.completeRegion().test(null));
    assertFalse(Regions.emptyRegion().test(null));
  }

  public void testSerialization() throws ClassNotFoundException, IOException {
    assertTrue(Serialization.copy(Regions.completeRegion()).test(null));
    assertFalse(Serialization.copy(Regions.emptyRegion()).test(null));
  }
}
