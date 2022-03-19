// code by jph
package ch.alpine.owl.math.region;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.ext.Serialization;

public class RegionsTest {
  @Test
  public void testSimple() {
    assertTrue(Regions.completeRegion().test(null));
    assertFalse(Regions.emptyRegion().test(null));
  }

  @Test
  public void testSerialization() throws ClassNotFoundException, IOException {
    assertTrue(Serialization.copy(Regions.completeRegion()).test(null));
    assertFalse(Serialization.copy(Regions.emptyRegion()).test(null));
  }
}
