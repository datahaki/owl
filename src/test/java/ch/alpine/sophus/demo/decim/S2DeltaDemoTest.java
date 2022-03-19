// code by jph
package ch.alpine.sophus.demo.decim;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class S2DeltaDemoTest {
  @Test
  public void testSimpleV1() {
    AbstractDemoHelper.offscreen(new S2DeltaDemo());
  }
}
