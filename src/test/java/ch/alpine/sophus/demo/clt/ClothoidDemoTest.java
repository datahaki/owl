// code by jph
package ch.alpine.sophus.demo.clt;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class ClothoidDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClothoidDemo());
  }
}
