// code by gjoel
package ch.alpine.owl.math.pursuit;

import java.util.Optional;
import java.util.stream.Stream;

import ch.alpine.sophus.clt.ClothoidBuilders;
import ch.alpine.sophus.ref.d1.CurveSubdivision;
import ch.alpine.sophus.ref.d1.LaneRiesenfeldCurveSubdivision;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.red.Nest;

public enum ClothoidPursuits {
  ;
  public static final CurveSubdivision CURVE_SUBDIVISION = //
      LaneRiesenfeldCurveSubdivision.of(ClothoidBuilders.SE2_ANALYTIC.clothoidBuilder(), 1);

  /** @param lookAhead of the form {x, y, heading}
   * @param depth
   * @return curve of ((1 << depth) + 1) points in SE(2) from origin {0, 0, 0} to given lookAhead */
  @Deprecated
  public static Tensor curve(Tensor lookAhead, int depth) {
    return Nest.of( //
        CURVE_SUBDIVISION::string, //
        Tensor.of(Stream.of(lookAhead.map(Scalar::zero), lookAhead)), //
        depth);
  }

  /** @param tensor waypoints
   * @param trajectoryEntryFinder strategy
   * @param var
   * @return GeodesicPursuit */
  public static PursuitInterface fromTrajectory( //
      Tensor tensor, TrajectoryEntryFinder trajectoryEntryFinder, Scalar var) {
    Optional<Tensor> lookAhead = trajectoryEntryFinder.on(tensor).apply(var).point();
    return lookAhead.isPresent() //
        ? ClothoidPursuit.of(lookAhead.get())
        : VoidPursuit.INSTANCE;
  }
}
