// code by jph
package ch.alpine.sophus.demo.misc;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class ColoredNoiseDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ColoredNoiseDemo());
  }
}
