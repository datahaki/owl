// code by jph
package ch.ethz.idsc.owl.rrts.adapter;

import java.io.IOException;

import ch.ethz.idsc.owl.math.region.Regions;
import ch.ethz.idsc.owl.rrts.core.TransitionRegionQuery;
import ch.ethz.idsc.tensor.RealScalar;
import ch.ethz.idsc.tensor.ext.Serialization;
import junit.framework.TestCase;

public class SampledTransitionRegionQueryTest extends TestCase {
  public void testSimple() throws ClassNotFoundException, IOException {
    TransitionRegionQuery trq = new SampledTransitionRegionQuery( //
        Regions.emptyRegion(), RealScalar.of(0.1));
    Serialization.copy(trq);
  }
}
