// code by jph
package ch.alpine.sophus.demo.bdn;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class R2ScatteredSetCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new R2ScatteredSetCoordinateDemo());
  }
}
