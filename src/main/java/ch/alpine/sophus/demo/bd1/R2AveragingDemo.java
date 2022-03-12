// code by jph
package ch.alpine.sophus.demo.bd1;

import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.tensor.Tensors;

/* package */ class R2AveragingDemo extends A2AveragingDemo {
  public R2AveragingDemo() {
    super(ManifoldDisplays.R2_H2);
    timerFrame.geometricComponent.setOffset(400, 400);
    // ---
    setControlPointsSe2(Tensors.fromString("{{0, 0, 1}, {1, 0, 1}, {-1, 1, 0}, {-0.5, -1, 0}, {0.4, 1, 0}}"));
  }

  public static void main(String[] args) {
    new R2AveragingDemo().setVisible(1300, 800);
  }
}
