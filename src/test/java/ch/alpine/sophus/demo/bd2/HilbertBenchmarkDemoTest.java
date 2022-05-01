// code by jph
package ch.alpine.sophus.demo.bd2;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

class HilbertBenchmarkDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new HilbertBenchmarkDemo());
  }
}
