// code by jph
package ch.alpine.sophus.app.opt;

import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.gds.ManifoldDisplays;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.win.WindowFunctions;
import junit.framework.TestCase;

public class GeodesicCausalFiltersTest extends TestCase {
  public void testSimple() {
    for (ManifoldDisplay manifoldDisplay : ManifoldDisplays.LIE_GROUPS)
      for (WindowFunctions smoothingKernel : WindowFunctions.values())
        for (int radius = 0; radius < 3; ++radius)
          for (GeodesicCausalFilters geodesicCausalFilters : GeodesicCausalFilters.values()) {
            TensorUnaryOperator tensorUnaryOperator = geodesicCausalFilters.supply(manifoldDisplay, smoothingKernel.get(), radius, RationalScalar.HALF);
            assertNotNull(tensorUnaryOperator);
          }
  }
}
