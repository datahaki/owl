// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2HermiteSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S2HermiteSubdivisionDemo());
  }
}
