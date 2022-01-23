// code by jph
package ch.alpine.owl.sim;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import ch.alpine.owl.math.state.StateTime;
import ch.alpine.owl.math.state.TrajectoryRegionQuery;
import ch.alpine.sophus.hs.r2.Se2Bijection;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.api.TensorUnaryOperator;
import ch.alpine.tensor.lie.TensorProduct;
import ch.alpine.tensor.lie.r2.AngleVector;
import ch.alpine.tensor.nrm.Vector2Norm;
import ch.alpine.tensor.red.Max;
import ch.alpine.tensor.red.Times;

// TODO implementation can be made more efficient
public class LidarRaytracer implements Serializable {
  private final Tensor directions;
  private final Scalar max_range;
  private final List<Tensor> localRays;

  /** @param angles vector
   * @param ranges vector with non-negative entries */
  public LidarRaytracer(Tensor angles, Tensor ranges) {
    directions = Tensor.of(angles.stream().map(Scalar.class::cast).map(AngleVector::of));
    max_range = ranges.stream().map(Scalar.class::cast).reduce(Max::of).orElseThrow();
    localRays = directions.stream().map(dir -> TensorProduct.of(ranges, dir)).collect(Collectors.toList());
  }

  /** @param trajectoryRegionQuery
   * @param stateTime
   * @return ranges as observed at given state-time */
  public Tensor scan(StateTime stateTime, TrajectoryRegionQuery trajectoryRegionQuery) {
    Scalar time = stateTime.time();
    Se2Bijection se2Bijection = new Se2Bijection(stateTime.state());
    TensorUnaryOperator forward = se2Bijection.forward();
    return Tensor.of(localRays.stream().parallel() //
        .map(ray -> ray.stream() //
            .filter(local -> trajectoryRegionQuery.test(new StateTime(forward.apply(local), time))) //
            .findFirst() //
            .map(Vector2Norm::of) //
            .orElse(max_range)));
  }

  /** @param scan vector obtained by {@link #scan(TrajectoryRegionQuery, StateTime)}
   * @return list of 2D-points */
  public Tensor toPoints(Tensor scan) {
    return Times.of(scan, directions);
  }
}
