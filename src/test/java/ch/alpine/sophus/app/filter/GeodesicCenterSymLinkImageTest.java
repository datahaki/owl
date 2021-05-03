// code by jph
package ch.alpine.sophus.app.filter;

import ch.alpine.sophus.app.sym.SymLinkImages;
import ch.alpine.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class GeodesicCenterSymLinkImageTest extends TestCase {
  public void testSmoothingKernel() {
    for (WindowFunctions smoothingKernel : WindowFunctions.values())
      for (int radius = 0; radius < 5; ++radius)
        SymLinkImages.ofGC(smoothingKernel.get(), radius);
  }
}
