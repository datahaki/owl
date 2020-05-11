// code by jph
package ch.ethz.idsc.sophus.app.subdiv;

import java.util.function.Function;

import ch.ethz.idsc.sophus.crv.subdiv.BSpline1CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline2CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline3CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline4CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline5CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.BSpline6CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.DodgsonSabinCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.DualC2FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.EightPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.FarSixPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.FourPointCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.HormannSabinCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeld3CurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.LaneRiesenfeldCurveSubdivision;
import ch.ethz.idsc.sophus.crv.subdiv.SixPointCurveSubdivision;
import ch.ethz.idsc.sophus.lie.rn.RnGeodesic;
import ch.ethz.idsc.sophus.math.GeodesicInterface;
import ch.ethz.idsc.tensor.Tensors;

/* package */ enum CurveSubdivisionSchemes {
  BSPLINE1(BSpline1CurveSubdivision::new), //
  BSPLINE2(BSpline2CurveSubdivision::new), //
  BSPLINE3(BSpline3CurveSubdivision::new), //
  BSPLINE3LR(LaneRiesenfeld3CurveSubdivision::of), //
  /** Hakenberg 2018 that uses 3 binary averages */
  BSPLINE4(CurveSubdivisionHelper::of), //
  /** Dyn/Sharon 2014 that uses 2 binary averages */
  BSPLINE4DS(BSpline4CurveSubdivision::dynSharon), //
  /** Alternative to Dyn/Sharon 2014 that also uses 2 binary averages */
  BSPLINE4S2(BSpline4CurveSubdivision::split2), BSPLINE5(BSpline5CurveSubdivision::new), //
  BSPLINE6(BSpline6CurveSubdivision::of), //
  LR1(gi -> LaneRiesenfeldCurveSubdivision.of(gi, 1)), //
  LR2(gi -> LaneRiesenfeldCurveSubdivision.of(gi, 2)), //
  LR3(gi -> LaneRiesenfeldCurveSubdivision.of(gi, 3)), //
  LR4(gi -> LaneRiesenfeldCurveSubdivision.of(gi, 4)), //
  LR5(gi -> LaneRiesenfeldCurveSubdivision.of(gi, 5)), //
  LR6(gi -> LaneRiesenfeldCurveSubdivision.of(gi, 6)), //
  DODGSON_SABIN(i -> DodgsonSabinCurveSubdivision.INSTANCE), //
  THREEPOINT(HormannSabinCurveSubdivision::of), //
  FOURPOINT(FourPointCurveSubdivision::new), //
  FOURPOINT2(CurveSubdivisionHelper::fps), //
  C2CUBIC(DualC2FourPointCurveSubdivision::cubic), //
  C2TIGHT(DualC2FourPointCurveSubdivision::tightest), //
  SIXPOINT(SixPointCurveSubdivision::new), //
  SIXFAR(FarSixPointCurveSubdivision::new), //
  EIGHTPOINT(EightPointCurveSubdivision::new), //
  ;

  private final Function<GeodesicInterface, CurveSubdivision> function;

  private CurveSubdivisionSchemes(Function<GeodesicInterface, CurveSubdivision> function) {
    this.function = function;
  }

  public CurveSubdivision of(GeodesicInterface geodesicInterface) {
    return function.apply(geodesicInterface);
  }

  public boolean isStringSupported() {
    try {
      function.apply(RnGeodesic.INSTANCE).string(Tensors.empty());
      return true;
    } catch (Exception exception) {
      // ---
    }
    return false;
  }
}
