// code by jph
package ch.alpine.sophus.demo.misc;

import ch.alpine.sophus.crv.se2c.EulerSpiral;

/* package */ class EulerSpiralDemo extends AbstractSpiralDemo {
  public EulerSpiralDemo() {
    super(EulerSpiral.FUNCTION);
  }

  public static void main(String[] args) {
    new EulerSpiralDemo().setVisible(1000, 600);
  }
}
