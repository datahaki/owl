// code by ob, jph
package ch.alpine.sophus.demo.opt;

import ch.alpine.sophus.api.Geodesic;
import ch.alpine.sophus.crv.spline.MonomialExtrapolationMask;
import ch.alpine.sophus.flt.bm.BiinvariantMeanExtrapolation;
import ch.alpine.sophus.flt.ga.GeodesicExtrapolation;
import ch.alpine.sophus.flt.ga.GeodesicFIRn;
import ch.alpine.sophus.flt.ga.GeodesicIIRn;
import ch.alpine.sophus.gds.ManifoldDisplay;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.api.ScalarUnaryOperator;
import ch.alpine.tensor.api.TensorUnaryOperator;

public enum GeodesicCausalFilters {
  GEODESIC_FIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay manifoldDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      Geodesic geodesicInterface = manifoldDisplay.geodesic();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      return GeodesicIIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  },
  GEODESIC_IIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay manifoldDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      Geodesic geodesicInterface = manifoldDisplay.geodesic();
      TensorUnaryOperator geodesicExtrapolation = GeodesicExtrapolation.of(geodesicInterface, smoothingKernel);
      return GeodesicFIRn.of(geodesicExtrapolation, geodesicInterface, radius, alpha);
    }
  },
  BIINVARIANT_MEAN_FIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay manifoldDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      TensorUnaryOperator geodesicExtrapolation = new BiinvariantMeanExtrapolation( //
          manifoldDisplay.biinvariantMean(), MonomialExtrapolationMask.INSTANCE);
      return GeodesicFIRn.of(geodesicExtrapolation, manifoldDisplay.geodesic(), radius, alpha);
    }
  },
  BIINVARIANT_MEAN_IIR {
    @Override
    public TensorUnaryOperator supply( //
        ManifoldDisplay manifoldDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha) {
      TensorUnaryOperator geodesicExtrapolation = new BiinvariantMeanExtrapolation( //
          manifoldDisplay.biinvariantMean(), MonomialExtrapolationMask.INSTANCE);
      return GeodesicIIRn.of(geodesicExtrapolation, manifoldDisplay.geodesic(), radius, alpha);
    }
  };

  public abstract TensorUnaryOperator supply( //
      ManifoldDisplay manifoldDisplay, ScalarUnaryOperator smoothingKernel, int radius, Scalar alpha);
}