// code by jph
package ch.alpine.sophus.demo.ref.d2;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class SurfaceMeshDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SurfaceMeshDemo());
  }
}
