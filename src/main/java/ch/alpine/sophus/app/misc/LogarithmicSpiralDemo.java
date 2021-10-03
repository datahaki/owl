// code by jph
package ch.alpine.sophus.app.misc;

import ch.alpine.sophus.crv.se2c.LogarithmicSpiral;

/* package */ class LogarithmicSpiralDemo extends AbstractSpiralDemo {
  public LogarithmicSpiralDemo() {
    super(LogarithmicSpiral.of(1, 0.2));
  }

  public static void main(String[] args) {
    new LogarithmicSpiralDemo().setVisible(1000, 600);
  }
}
