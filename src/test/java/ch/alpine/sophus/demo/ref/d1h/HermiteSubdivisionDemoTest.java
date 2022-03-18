// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class HermiteSubdivisionDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HermiteSubdivisionDemo());
  }
}
