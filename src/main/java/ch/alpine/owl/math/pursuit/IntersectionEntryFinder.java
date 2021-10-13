// code by gjoel
package ch.alpine.owl.math.pursuit;

import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.sophus.math.Extract2D;
import ch.alpine.sophus.math.MinMax;
import ch.alpine.tensor.RationalScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.itp.Interpolation;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.nrm.Vector2Norm;

public final class IntersectionEntryFinder extends TrajectoryEntryFinder implements Serializable {
  public static final TrajectoryEntryFinder SPHERE_RN = new IntersectionEntryFinder(SphereCurveIntersection::new);
  public static final TrajectoryEntryFinder SPHERE_SE2 = new IntersectionEntryFinder(SphereSe2CurveIntersection::new);
  // ---
  private final ScalarAssistedCurveIntersectionFunction function;

  public IntersectionEntryFinder(ScalarAssistedCurveIntersectionFunction function) {
    this.function = function;
  }

  @Override // from TrajectoryEntryFinder
  public TrajectoryEntry protected_apply(Tensor waypoints, Scalar radius) {
    return new TrajectoryEntry(function.apply(radius).string(waypoints).orElse(null), radius);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    MinMax minmax = MinMax.of(Tensor.of(waypoints.stream().map(Extract2D.FUNCTION).map(Vector2Norm::of)));
    Interpolation interpolation = LinearInterpolation.of(minmax.matrix());
    return IntStream.range(0, waypoints.length()) //
        .mapToObj(i -> RationalScalar.of(i, waypoints.length() - 1)) //
        .map(interpolation::At);
  }
}
