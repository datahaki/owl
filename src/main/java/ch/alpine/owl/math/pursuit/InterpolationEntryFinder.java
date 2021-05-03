// code by gjoel
package ch.alpine.owl.math.pursuit;

import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.itp.LinearInterpolation;
import ch.alpine.tensor.sca.Clip;
import ch.alpine.tensor.sca.Clips;

public class InterpolationEntryFinder extends TrajectoryEntryFinder implements Serializable {
  public static final TrajectoryEntryFinder INSTANCE = new InterpolationEntryFinder();

  /***************************************************/
  private InterpolationEntryFinder() {
    // ---
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar index) {
    Clip clip = Clips.positive(waypoints.length() - 1);
    return new TrajectoryEntry(clip.isInside(index) //
        ? LinearInterpolation.of(waypoints).at(index)
        : null, index);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
