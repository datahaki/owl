// code by jph
package ch.alpine.sophus.demo.decim;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class BulkDecimationDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BulkDecimationDemo());
  }
}
