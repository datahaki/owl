// code by jph
package ch.alpine.sophus.demo.ref.d1h;

import ch.alpine.sophus.demo.AbstractDemoHelper;
import junit.framework.TestCase;

public class SeriesHermiteSubdivisionDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new SeriesHermiteSubdivisionDemo());
  }
}
