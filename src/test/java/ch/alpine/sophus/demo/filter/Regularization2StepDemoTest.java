// code by jph
package ch.alpine.sophus.demo.filter;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.AbstractDemoHelper;

public class Regularization2StepDemoTest {
  @Test
  public void testSimple() {
    AbstractDemoHelper.offscreen(new Regularization2StepDemo());
  }
}
