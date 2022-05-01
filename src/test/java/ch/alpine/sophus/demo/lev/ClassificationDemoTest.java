// code by jph
package ch.alpine.sophus.demo.lev;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class ClassificationDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new ClassificationDemo());
  }
}
