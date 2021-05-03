// code by ob, jph
package ch.alpine.sophus.opt;

import ch.alpine.sophus.bm.BiinvariantMean;
import ch.alpine.sophus.flt.bm.BiinvariantMeanCenter;
import ch.alpine.sophus.flt.ga.GeodesicCenter;
import ch.alpine.sophus.flt.ga.GeodesicCenterMidSeeded;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.sophus.math.Geodesic;
import ch.alpine.sophus.math.SplitInterface;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;

/** in the current implementation all filters have the same performance for an arbitrary radius */
public enum GeodesicFilters {
  GEODESIC {
    @Override
    public TensorUnaryOperator supply( //
        SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean) {
      return GeodesicCenter.of(splitInterface, smoothingKernel);
    }
  },
  GEODESIC_MID {
    @Override
    public TensorUnaryOperator supply( //
        SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean) {
      return GeodesicCenterMidSeeded.of(splitInterface, smoothingKernel);
    }
  },
  BIINVARIANT_MEAN {
    @Override
    public TensorUnaryOperator supply( //
        SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean) {
      return BiinvariantMeanCenter.of(biinvariantMean, smoothingKernel);
    }
  };

  /** @param splitInterface
   * @param smoothingKernel
   * @param biinvariantMean
   * @return */
  public abstract TensorUnaryOperator supply( //
      SplitInterface splitInterface, ScalarUnaryOperator smoothingKernel, BiinvariantMean biinvariantMean);

  /** @param geodesicDisplay
   * @param smoothingKernel
   * @return */
  public TensorUnaryOperator from(ManifoldDisplay geodesicDisplay, ScalarUnaryOperator smoothingKernel) {
    Geodesic geodesicInterface = geodesicDisplay.geodesicInterface();
    BiinvariantMean biinvariantMean = geodesicDisplay.biinvariantMean();
    return supply(geodesicInterface, smoothingKernel, biinvariantMean);
  }
}