// code by jph
package ch.alpine.tensor.demo;

import ch.alpine.sophus.gui.win.AbstractDemoHelper;
import junit.framework.TestCase;

public class NdTreeMapDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new NdTreeMapDemo());
  }
}
