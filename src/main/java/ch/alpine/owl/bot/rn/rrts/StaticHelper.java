// code by jph
package ch.alpine.owl.bot.rn.rrts;

import ch.alpine.owl.bot.r2.R2NoiseRegion;
import ch.alpine.owl.math.region.PolygonRegion;
import ch.alpine.owl.rrts.adapter.SampledTransitionRegionQuery;
import ch.alpine.owl.rrts.core.TransitionRegionQuery;
import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Tensors;

/* package */ enum StaticHelper {
  ;
  public static TransitionRegionQuery polygon1() {
    return new SampledTransitionRegionQuery( //
        PolygonRegion.numeric(Tensors.matrix(new Number[][] { //
            { 3, 1 }, //
            { 4, 1 }, //
            { 4, 6 }, //
            { 1, 6 }, //
            { 1, 3 }, //
            { 3, 3 } //
        })), RealScalar.of(0.1));
  }

  public static TransitionRegionQuery noise1() {
    return new SampledTransitionRegionQuery( //
        new R2NoiseRegion(RealScalar.of(0.4)), RealScalar.of(0.1));
  }
}
