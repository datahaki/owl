// code by jph
package ch.alpine.sophus.demo.ref.d1;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class SymMaskImagesTest {
  @Test
  public void testSimple() {
    SymMaskImages[] values = SymMaskImages.values();
    assertTrue(5 < values.length);
  }
}
