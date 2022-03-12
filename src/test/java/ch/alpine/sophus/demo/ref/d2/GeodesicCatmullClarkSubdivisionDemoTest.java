// code by jph
package ch.alpine.sophus.demo.ref.d2;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class GeodesicCatmullClarkSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new GeodesicCatmullClarkSubdivisionDemo());
  }
}
