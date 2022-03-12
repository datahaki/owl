// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class HermiteSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HermiteSubdivisionDemo());
  }
}
