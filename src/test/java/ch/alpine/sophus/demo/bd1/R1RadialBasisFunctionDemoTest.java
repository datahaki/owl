// code by jph
package ch.alpine.sophus.demo.bd1;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class R1RadialBasisFunctionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R1RadialBasisFunctionDemo());
  }
}
