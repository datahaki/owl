// code by jph
package ch.alpine.tensor.demo.nd;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class NdTreeMapDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new NdTreeMapDemo());
  }
}
