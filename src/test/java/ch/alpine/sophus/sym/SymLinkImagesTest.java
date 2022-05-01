// code by jph
package ch.alpine.sophus.sym;

import org.junit.jupiter.api.Test;

import ch.alpine.tensor.sca.win.WindowFunctions;

class SymLinkImagesTest {
  @Test
  public void testSmoothingKernel() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values())
      for (int radius = 0; radius < 5; ++radius)
        SymLinkImages.ofGC(smoothingKernel.get(), radius);
  }
}
