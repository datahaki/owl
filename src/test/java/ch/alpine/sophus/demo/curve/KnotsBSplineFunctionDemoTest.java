// code by jph
package ch.alpine.sophus.demo.curve;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class KnotsBSplineFunctionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new KnotsBSplineFunctionDemo());
  }
}
