// code by jph
package ch.alpine.sophus.demo.ext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import ch.alpine.sophus.ext.api.GeodesicCausalFilters;
import ch.alpine.sophus.ext.dis.ManifoldDisplay;
import ch.alpine.sophus.ext.dis.ManifoldDisplays;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.sca.win.WindowFunctions;

class GeodesicCausalFiltersTest {
  @Test
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
