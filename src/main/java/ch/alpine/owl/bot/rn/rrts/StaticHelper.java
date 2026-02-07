// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.sophis.crv.d2.alg.PolygonRegion;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;
import ch.alpine.tensor.sca.N;

/* package */ enum StaticHelper {
  ;
  public static TransitionRegionQuery polygon1() {
    return new SampledTransitionRegionQuery( //
        new PolygonRegion(Tensors.matrix(new Number[][] { //
            { 3, 1 }, //
            { 4, 1 }, //
            { 4, 6 }, //
            { 1, 6 }, //
            { 1, 3 }, //
            { 3, 3 } //
        }).map(N.DOUBLE)), RealScalar.of(0.1));
  }

  public static TransitionRegionQuery noise1() {
    return new SampledTransitionRegionQuery( //
        new R2NoiseRegion(RealScalar.of(0.4)), RealScalar.of(0.1));
  }
}
