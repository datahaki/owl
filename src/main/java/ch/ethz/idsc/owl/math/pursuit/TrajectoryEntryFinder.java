// code by gjoel
package ch.ethz.idsc.owl.math.pursuit;

import java.util.function.Function;
import java.util.stream.Stream;

import ch.ethz.idsc.tensor.Scalar;
import ch.ethz.idsc.tensor.Tensor;

public abstract class TrajectoryEntryFinder {
  /** @param waypoints of trajectory
   * @return function to be applied on waypoints */
  public final Function<Scalar, TrajectoryEntry> on(Tensor waypoints) {
    return scalar -> protected_apply(waypoints, scalar);
  }

  /** @param waypoints of trajectory
   * @return stream of coarsely distributed trajectory entries */
  public final Stream<TrajectoryEntry> sweep(Tensor waypoints) {
    return sweep_variables(waypoints).map(on(waypoints));
  }

  /** @param waypoints of trajectory
   * @return stream of indices for given waypoints to visit */
  protected abstract Stream<Scalar> sweep_variables(Tensor waypoints);

  /** @param waypoints of trajectory
   * @param var to specify entry point choice
   * @return TrajectoryEntry */
  protected abstract TrajectoryEntry protected_apply(Tensor waypoints, Scalar var);
}
