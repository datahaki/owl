// code by jph
package ch.ethz.idsc.sophus.app.bd1;

import ch.ethz.idsc.sophus.app.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class R2KrigingDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2KrigingDemo());
  }
}
