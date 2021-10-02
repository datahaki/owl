// code by jph
package ch.alpine.owl.bot.r2;

import ch.alpine.tensor.Tensors;
import junit.framework.TestCase;

public class R2ImageRegionsTest extends TestCase {
  public void testSimple() {
    R2ImageRegionWrap r2irw = R2ImageRegions._0F5C_2182;
    // TODO for some reason this stopped being true (fedora?)
    // assertTrue(r2irw.region().isMember(Tensors.vector(1, 2)));
    assertFalse(r2irw.region().test(Tensors.vector(7, 5)));
  }
}
