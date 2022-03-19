// code by jph
package ch.alpine.sophus.demo.ref.d1;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class CurveSubdivisionDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new CurveSubdivisionDemo());
  }
}
