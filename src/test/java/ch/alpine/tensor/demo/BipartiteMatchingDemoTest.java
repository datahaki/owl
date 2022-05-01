// code by jph
package ch.alpine.tensor.demo;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class BipartiteMatchingDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new BipartiteMatchingDemo());
  }
}
