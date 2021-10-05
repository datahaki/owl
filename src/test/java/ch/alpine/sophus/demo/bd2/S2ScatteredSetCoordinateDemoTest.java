// code by jph
package ch.alpine.sophus.demo.bd2;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class S2ScatteredSetCoordinateDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new PlanarScatteredSetCoordinateDemo());
  }
}
