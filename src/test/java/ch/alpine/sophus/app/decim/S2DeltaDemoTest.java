// code by jph
package ch.alpine.sophus.app.decim;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2DeltaDemoTest extends TestCase {
  public void testSimpleV1() {
    AbstractDemoHelper.offscreen(new S2DeltaDemo());
  }
}
