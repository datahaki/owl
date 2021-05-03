// code by gjoel
package ch.alpine.owl.math.pursuit;

import java.io.Serializable;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import ch.alpine.tensor.RealScalar;
import ch.alpine.tensor.Scalar;
import ch.alpine.tensor.Tensor;
import ch.alpine.tensor.sca.Floor;

public final class NaiveEntryFinder extends TrajectoryEntryFinder implements Serializable {
  public static final TrajectoryEntryFinder INSTANCE = new NaiveEntryFinder();

  private NaiveEntryFinder() {
    // ---
  }

  @Override // from TrajectoryEntryFinder
  protected TrajectoryEntry protected_apply(Tensor waypoints, Scalar var) {
    int index = Floor.intValueExact(var);
    return new TrajectoryEntry(0 <= index && index < waypoints.length() //
        ? waypoints.get(index)
        : null, var);
  }

  @Override // from TrajectoryEntryFinder
  protected Stream<Scalar> sweep_variables(Tensor waypoints) {
    return IntStream.range(0, waypoints.length()).mapToObj(RealScalar::of);
  }
}
