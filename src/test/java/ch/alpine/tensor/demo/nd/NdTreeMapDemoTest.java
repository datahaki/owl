// code by jph
package ch.alpine.tensor.demo.nd;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;
import junit.framework.TestCase;

public class NdTreeMapDemoTest extends TestCase {
  public void testSimple() {
    AbstractDemoHelper.offscreen(new NdTreeMapDemo());
  }
}
