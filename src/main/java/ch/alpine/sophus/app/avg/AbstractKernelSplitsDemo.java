// code by jph
package ch.alpine.sophus.app.avg;

import java.awt.Dimension;
import java.util.Arrays;

import ch.alpine.java.awt.SpinnerLabel;
import ch.alpine.tensor.sca.win.WindowFunctions;

/* package */ abstract class AbstractKernelSplitsDemo extends AbstractSplitsDemo {
  final SpinnerLabel<WindowFunctions> spinnerKernel = new SpinnerLabel<>();

  public AbstractKernelSplitsDemo() {
    spinnerKernel.setList(Arrays.asList(WindowFunctions.values()));
    spinnerKernel.setValue(WindowFunctions.DIRICHLET);
    spinnerKernel.addToComponentReduced(timerFrame.jToolBar, new Dimension(180, 28), "filter");
  }
}
