// code by jph
package ch.alpine.sophus.demo.ref.d2;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class SurfaceMeshDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SurfaceMeshDemo());
  }
}
