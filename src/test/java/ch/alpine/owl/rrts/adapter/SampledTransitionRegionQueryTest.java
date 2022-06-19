// code by jph
package ch.alpine.owl.rrts.adapter;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import ch.alpine.owl.math.region.Regions;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.ext.Serialization;

class SampledTransitionRegionQueryTest {
  @Test
  void testSimple() throws ClassNotFoundException, IOException {
    TransitionRegionQuery trq = new SampledTransitionRegionQuery( //
        Regions.emptyRegion(), RealScalar.of(0.1));
    Serialization.copy(trq);
  }
}
