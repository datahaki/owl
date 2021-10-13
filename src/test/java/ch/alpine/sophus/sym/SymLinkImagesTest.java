// code by jph
package ch.alpine.sophus.sym;

import ch.alpine.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class SymLinkImagesTest extends TestCase {
  public void testSmoothingKernel() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values())
      for (int radius = 0; radius < 5; ++radius)
        SymLinkImages.ofGC(smoothingKernel.get(), radius);
  }
}
