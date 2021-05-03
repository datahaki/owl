// code by jph
package ch.alpine.sophus.app.decim;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class BulkDecimationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BulkDecimationDemo());
  }
}
