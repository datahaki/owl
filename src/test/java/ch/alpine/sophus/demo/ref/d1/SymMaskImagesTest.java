// code by jph
package ch.alpine.sophus.demo.ref.d1;

import junit.framework.TestCase;

public class SymMaskImagesTest extends TestCase {
  public void testSimple() {
    SymMaskImages[] values = SymMaskImages.values();
    assertTrue(5 < values.length);
  }
}
