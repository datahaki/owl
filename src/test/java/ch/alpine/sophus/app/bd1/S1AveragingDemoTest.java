// code by jph
package ch.alpine.sophus.app.bd1;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class S1AveragingDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new S1KrigingDemo());
  }
}
